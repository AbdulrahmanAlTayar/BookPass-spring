import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Link, useNavigate } from 'react-router-dom';
import Logo from '../assets/Logo';

const Login = () => {
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        rememberMe: false,
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const { signIn } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === 'checkbox' ? checked : value,
        });
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!formData.email || !formData.password) {
            setError('يرجى إدخال البريد الإلكتروني وكلمة المرور');
            return;
        }

        setLoading(true);

        const { data, error: signInError } = await signIn(
            formData.email,
            formData.password
        );

        setLoading(false);

        if (signInError) {
            if (signInError.message.includes('Invalid login credentials')) {
                setError('البريد الإلكتروني أو كلمة المرور غير صحيحة');
            } else if (signInError.message.includes('Email not confirmed')) {
                setError('يرجى تأكيد بريدك الإلكتروني قبل تسجيل الدخول');
            } else {
                setError(signInError.message);
            }
            return;
        }

        navigate('/');
    };

    return (
	 <div className="auth-page">
	    <div className="auth-image"></div>

      <div className="auth-form-container">
        <div className="auth-logo">
          <Logo />
        </div>

        <div className="auth-form-wrapper">
          <h1>مرحباً بعودتك</h1>

          {error && <div className="alert alert-error">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="email">البريد الإلكتروني</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="أدخل بريدك الإلكتروني"
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">كلمة المرور</label>
              <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="أدخل كلمة المرور"
                disabled={loading}
              />
            </div>

            <div className="form-group form-row">
              <label className="checkbox-label">
                <input
                  type="checkbox"
                  name="rememberMe"
                  checked={formData.rememberMe}
                  onChange={handleChange}
                />
                <span>تذكرني</span>
              </label>
              <Link to="/forgot-password" className="forgot-link">
                نسيت كلمة المرور؟
              </Link>
            </div>

            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'جاري الدخول...' : 'تسجيل الدخول'}
            </button>
          </form>

          <p className="auth-footer">
            ليس لديك حساب؟ <Link to="/register">سجل الآن</Link>
          </p>
        </div>
      </div>
    </div>
    );
};

export default Login;
