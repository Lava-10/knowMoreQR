import React from 'react';
import { Route, Redirect, RouteProps, RouteComponentProps } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface ProtectedRouteProps extends Omit<RouteProps, 'component'> {
    component: React.ComponentType<any>;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ component: Component, ...rest }) => {
    const { isAuthenticated, isLoading } = useAuth();

    if (isLoading) {
        // Optionally show a loading spinner or similar while auth state is being determined
        return <div>Loading...</div>; 
    }

    return (
        <Route
            {...rest}
            render={(props: RouteComponentProps) =>
                isAuthenticated ? (
                    <Component {...props} />
                ) : (
                    <Redirect to="/login" />
                )
            }
        />
    );
};

export default ProtectedRoute; 