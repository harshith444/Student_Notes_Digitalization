package proj;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/student_notes";
    private static final String USER = "root";
    private static final String PASS = "Learnnew@248";

    
    public static boolean validateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

   
    public static boolean isAdmin(String username) {
        String query = "SELECT isAdmin FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("isAdmin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    

  
    public static List<Note> getNotesByUsername(String username) throws SQLException {
        List<Note> notes = new ArrayList<>();
        String query = "SELECT n.id, n.note FROM notes n JOIN users u ON n.user_id = u.id WHERE u.username = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String content = rs.getString("note");
                notes.add(new Note(id, content));
            }
        }
        return notes;
    }

   
    public static void saveNoteForUser(String username, String noteContent) throws SQLException {
        String query = "INSERT INTO notes (user_id, note) VALUES ((SELECT id FROM users WHERE username = ?), ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, noteContent);
            stmt.executeUpdate();
        }
    }

    
    public static void updateNoteContent(int noteId, String noteContent) throws SQLException {
        String query = "UPDATE notes SET note = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, noteContent);
            stmt.setInt(2, noteId);
            stmt.executeUpdate();
        }
    }

    
    public static Note getLatestNoteForUser(String username) throws SQLException {
        String query = "SELECT n.id, n.note FROM notes n JOIN users u ON n.user_id = u.id WHERE u.username = ? ORDER BY n.id DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String content = rs.getString("note");
                return new Note(id, content);
            }
        }
        return null;
    }
    
    public static void deleteNoteById(int noteId) throws SQLException {
        String query = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, noteId);
            stmt.executeUpdate();
        }
    }
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
  public static List<String> getAllUsers() throws SQLException {
     List<String> users = new ArrayList<>();
     String query = "SELECT username FROM users"; 

     try (Connection conn = getConnection();
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(query)) {

         while (rs.next()) {
             users.add(rs.getString("username"));
         }
     }
     return users;
 }

 
     
    public static List<String> getAllNotes() {
        List<String> data = new ArrayList<>();
        String query = "SELECT u.username, n.note FROM users u JOIN notes n ON u.id = n.user_id";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String username = rs.getString("username");
                String note = rs.getString("note");
                data.add("User: " + username + " - Note: " + note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}
