public class AuthorizationService {
    public static boolean isAdmin(User currentUser) {
        if (currentUser == null) {
            return false;
        }

        return currentUser.getRole().equalsIgnoreCase("admin");
    }
    public static boolean isStudent(User currentUser) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRole().equalsIgnoreCase("student");
    }

    public static boolean isInstructor(User currentUser) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRole().equalsIgnoreCase("instructor");
    }
}