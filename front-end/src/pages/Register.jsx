import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Link, useNavigate } from 'react-router-dom';
import Logo from '../assets/Logo';

const Register = () => {
    const [formData, setFormData] = useState({
        fullName: '',
        email: '',
        password: '',
        iban: '',
        agreeTerms: false,
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const { signUp } = useAuth();
    const navigate = useNavigate();

    const validatePassword = (password) => {
        if (password.length < 8) return 'كلمة المرور يجب أن تكون 8 أحرف على الأقل';
        if (!/[A-Z]/.test(password)) return 'كلمة المرور يجب أن تحتوي على حرف كبير';
        if (!/[a-z]/.test(password)) return 'كلمة المرور يجب أن تحتوي على حرف صغير';
        if (!/[0-9]/.test(password)) return 'كلمة المرور يجب أن تحتوي على رقم';
        return null;
    };

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
        setSuccess('');

        if (!formData.fullName || !formData.email || !formData.password) {
            setError('جميع الحقول مطلوبة');
            return;
        }

        if (!formData.agreeTerms) {
            setError('يجب الموافقة على سياسة الخصوصية والشروط');
            return;
        }

        const passwordError = validatePassword(formData.password);
        if (passwordError) {
            setError(passwordError);
            return;
        }

        setLoading(true);

        const { data, error: signUpError } = await signUp(
            formData.email,
            formData.password,
            formData.fullName
        );

        setLoading(false);

        if (signUpError) {
            setError(signUpError.message);
            return;
        }

        setSuccess('تم التسجيل بنجاح! يرجى التحقق من بريدك الإلكتروني');
    };

    return (
	     <div className="auth-page">
      <div className="auth-image"></div>

      <div className="auth-form-container">
        <div className="auth-logo">
          <Logo />
        </div>

        <div className="auth-form-wrapper">
          <h1>انضم إلينا الآن وابدأ تسوقك</h1>

          {error && <div className="alert alert-error">{error}</div>}
          {success && <div className="alert alert-success">{success}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="fullName">اسم المستخدم</label>
              <input
                type="text"
                id="fullName"
                name="fullName"
                value={formData.fullName}
                onChange={handleChange}
                placeholder="أدخل اسمك"
                disabled={loading}
              />
            </div>

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

            <div className="form-group">
              <label htmlFor="iban">رقم حساب البنكي للبائع IBAN (إختياري)</label>
              <input
                type="text"
                id="iban"
                name="iban"
                value={formData.iban}
                onChange={handleChange}
                placeholder="SA00 0000 0000 0000 0000 0000"
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label className="checkbox-label">
                <input
                  type="checkbox"
                  name="agreeTerms"
                  checked={formData.agreeTerms}
                  onChange={handleChange}
                />
                <span>أقبل سياسة الخصوصية والشروط</span>
              </label>
            </div>

            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'جاري التسجيل...' : 'التسجيل'}
            </button>
          </form>

          <p className="auth-footer">
	   لديك حساب مسجل ؟ <Link to="/login">سجل الدخول</Link>
          </p>
        </div>
      </div>
    </div>
    );
};

export default Register;
