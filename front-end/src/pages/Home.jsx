import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const Home = () => {
    const { user, signOut } = useAuth();
    const navigate = useNavigate();

    const handleSignOut = async () => {
        await signOut();
        navigate('/login');
    };

    return (
	    <div className="home-container">
      <h1>Welcome to BookPass!</h1>
      {user && (
        <div className="user-info">
          <p>Logged in as: <strong>{user.email}</strong></p>
          <p>Name: <strong>{user.user_metadata?.full_name || 'N/A'}</strong></p>
          <button onClick={handleSignOut} className="btn btn-secondary">
            Sign Out
          </button>
        </div>
      )}
    </div>
    );
};

export default Home;
