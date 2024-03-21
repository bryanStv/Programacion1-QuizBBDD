package quiz;
import java.sql.*;
import java.util.Scanner;

public class Main {
    private static java.sql.Connection con;
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static int puntuacion = 0;
    public static void main(String[] args) throws SQLException {
        String host = "jdbc:sqlite:src/main/resources/quizbbdd";
        con = java.sql.DriverManager.getConnection(host);
        menu();
    }

    private static void menu() throws SQLException {
        banner();
        System.out.println(ANSI_CYAN+"███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████");
        System.out.println("██                                                        Iniciar Sesión(1) | Ranking(2)                                                             ██");
        System.out.println("███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████");
        System.out.print("----------------------------------------------------> "+ANSI_RESET);
        Scanner tc = new Scanner(System.in);
        int option = tc.nextInt();
        tc.nextLine();
        switch (option){
            case 1:
                System.out.print(ANSI_PURPLE+"Introduce tu nombre de usuario: "+ANSI_RESET);
                String user = tc.nextLine();
                if(userExiste(user)){
                    login(user);
                }else{
                    System.out.print(ANSI_PURPLE+"El usuario no existe, quieres registrarte(S/N)? "+ANSI_RESET);
                    String ans = tc.nextLine();
                    if(ans.equals("N") || ans.equals("n")){
                        System.out.println(ANSI_PURPLE+"ADIÓS"+ANSI_RESET);
                        System.exit(0);
                    } else if (ans.equals("S") || ans.equals("s")) {
                        registro(user);
                        login(user);
                        break;
                    }else{
                        System.out.println(ANSI_PURPLE+"Por favor, introduce una opción correcta"+ANSI_RESET);
                        menu();
                    }
                }
                break;
            case 2:
                rankings();
                break;
            default:
                System.out.println(ANSI_PURPLE+"Elige una opción correcta (1|2)"+ANSI_RESET);
                menu();
        }
    }
    private static void login(String user) throws SQLException{
        System.out.println(ANSI_CYAN+"███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████");
        System.out.println("██                                                  Jugar(1) | Ver puntuación máxima(2) | Salir(3)                                                   ██");
        System.out.println("███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████");
        System.out.printf("-----------------------------> ██Bienvenido, %s██> %s",user,ANSI_RESET);
        Scanner tc = new Scanner(System.in);
        int option = tc.nextInt();
        switch (option){
            case 1:
                jugar(user);
                break;
            case 2:
                puntuacionMaximaUser(user);
                login(user);
            case 3:
                menu();
            default:
                System.out.println(ANSI_PURPLE+"Elige una opción correcta."+ANSI_RESET);
                login(user);
        }
    }
    private static void rankings() throws SQLException{
        String query = "select * from scores order by scorePoints DESC,hora DESC";
        PreparedStatement st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        int contador = 1;
        System.out.print(ANSI_PURPLE);
        System.out.println("███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████");
        while(rs.next()) {
            System.out.printf("\t\t\t\t\t\t\t\t\t    Puesto %d --> Nick: %s %d.pts fecha: %s\n",contador,rs.getString("nick"),rs.getInt("scorePoints"),rs.getString("hora"));
            contador++;
        }
        System.out.println("███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████");
        System.out.println(ANSI_RESET);
    }

    private static void puntuacionMaximaUser(String user) throws SQLException{
        String query = "select * from scores s where s.idUser = ? order by s.scorePoints DESC limit 1";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1,getIDuser(user));
        ResultSet rs = st.executeQuery();
        System.out.println(ANSI_PURPLE);
        System.out.println("                                                                     PUNTUACIÓN MÁXIMA                                                                 ");
        System.out.println("███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████");
        while(rs.next()){
            System.out.printf("\t\t\t\t\t\t\t\t\t    Nick: %s %d.pts fecha: %s\n",rs.getString("nick"),rs.getInt("ScorePoints"),rs.getString("hora"));
        }
        System.out.println("███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████");
        System.out.println(ANSI_RESET);
    }

    private static void jugar(String user) throws SQLException{
        Scanner tc = new Scanner(System.in);
        int respuesta;
        String query = "select * from questions";
        PreparedStatement st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        int fallos = 0;
        System.out.print(ANSI_PURPLE);
        System.out.println("███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████");
        while(rs.next()){
            System.out.printf("Pregunta %d --> %s\n\tOpciones:\n\t\t1: %s , 2: %s, 3: %s\n",rs.getInt(1),rs.getString(2),rs.getString(4),rs.getString(5),rs.getString(6));
            System.out.printf("\t\tDime tu respuesta(fallos: %s): ",fallos);
            respuesta = tc.nextInt();
            if(respuesta == 1){
                respuesta = 4;
            }else if(respuesta == 2){
                respuesta = 5;
            }else if(respuesta == 3){
                respuesta = 6;
            }else{
                System.out.print("Error, elige otra opción válida(saliendo)");
                System.out.println("███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████");
                System.out.println(ANSI_RESET);
                System.exit(0);
            }
            if(fallos == 3){
                break;
            }
            if(rs.getString(3).equals(rs.getString(respuesta))){
                System.out.println("CORRECTO");
                puntuacion+=10;
            }else{
                fallos++;
                System.out.println("INCORRECTO");
            }
        }
        addPoints(user);
        System.out.println("███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████");
        System.out.println(ANSI_RESET);
        puntuacionMaximaUser(user);
    }

    private static void addPoints(String user) throws SQLException{
        resetPoints(user);
        PreparedStatement st = null;
        String query = String.format("update player set scorePoints = scorePoints + %d where nick = ?",puntuacion);
        st = con.prepareStatement(query);
        st.setString(1,user);
        st.executeUpdate();
        addRanking(user);
    }

    private static void resetPoints(String user) throws SQLException{
        PreparedStatement st = null;
        String query = "update player set scorePoints = 0 where nick = ?";
        st = con.prepareStatement(query);
        st.setString(1,user);
        st.executeUpdate();
    }

    private static void addRanking(String user) throws SQLException{
        int idUser = getIDuser(user);
        PreparedStatement st = null;
        String query = "INSERT INTO scores(nick,scorePoints,idUser) VALUES(?,?,?)";
        st = con.prepareStatement(query);
        st.setString(1,user);
        st.setInt(2,puntuacion);
        st.setInt(3,idUser);
        st.executeUpdate();
    }

    private static void registro(String user) throws SQLException{
        Scanner tc = new Scanner(System.in);
        PreparedStatement st = null;
        System.out.print("Nick: ");
        String name = tc.nextLine();
        String query = "INSERT INTO player(nick) VALUES(?)";
        st = con.prepareStatement(query);
        st.setString(1,user);
        st.executeUpdate();
    }

    private static boolean userExiste(String user) throws SQLException{
        String query = "select nick from player where nick = ?";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1,user);
        ResultSet rs = st.executeQuery();
        return rs.next();
    }

    private static int getIDuser(String user) throws SQLException{
        String query = "select idUser from player where nick = ?";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1,user);
        ResultSet rs = st.executeQuery();
        return rs.getInt("idUser");
    }

    private static void banner(){
        System.out.println(ANSI_PURPLE+"\n" +
                "███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████\n" +
                "█░░░░░░░░░░░░░░███░░░░░░██░░░░░░█░░░░░░░░░░█░░░░░░░░░░░░░░░░░░████░░░░░░░░░░░░░░██████████░░░░░░░░░░░░░░██████████░░░░░░░░░░░░██████████░░░░░░░░░░░░███\n" +
                "█░░▄▀▄▀▄▀▄▀▄▀░░███░░▄▀░░██░░▄▀░░█░░▄▀▄▀▄▀░░█░░▄▀▄▀▄▀▄▀▄▀▄▀▄▀░░████░░▄▀▄▀▄▀▄▀▄▀░░██████████░░▄▀▄▀▄▀▄▀▄▀░░██████████░░▄▀▄▀▄▀▄▀░░░░████████░░▄▀▄▀▄▀▄▀░░░░█\n" +
                "█░░▄▀░░░░░░▄▀░░███░░▄▀░░██░░▄▀░░█░░░░▄▀░░░░█░░░░░░░░░░░░▄▀▄▀░░████░░▄▀░░░░░░▄▀░░██████████░░▄▀░░░░░░▄▀░░██████████░░▄▀░░░░▄▀▄▀░░████████░░▄▀░░░░▄▀▄▀░░█\n" +
                "█░░▄▀░░██░░▄▀░░███░░▄▀░░██░░▄▀░░███░░▄▀░░███████████░░░░▄▀░░░░████░░▄▀░░██░░▄▀░░██████████░░▄▀░░██░░▄▀░░██████████░░▄▀░░██░░▄▀░░████████░░▄▀░░██░░▄▀░░█\n" +
                "█░░▄▀░░██░░▄▀░░███░░▄▀░░██░░▄▀░░███░░▄▀░░█████████░░░░▄▀░░░░██████░░▄▀░░░░░░▄▀░░░░████████░░▄▀░░░░░░▄▀░░░░████████░░▄▀░░██░░▄▀░░████████░░▄▀░░██░░▄▀░░█\n" +
                "█░░▄▀░░██░░▄▀░░███░░▄▀░░██░░▄▀░░███░░▄▀░░███████░░░░▄▀░░░░████████░░▄▀▄▀▄▀▄▀▄▀▄▀░░████████░░▄▀▄▀▄▀▄▀▄▀▄▀░░████████░░▄▀░░██░░▄▀░░████████░░▄▀░░██░░▄▀░░█\n" +
                "█░░▄▀░░██░░▄▀░░███░░▄▀░░██░░▄▀░░███░░▄▀░░█████░░░░▄▀░░░░██████████░░▄▀░░░░░░░░▄▀░░████████░░▄▀░░░░░░░░▄▀░░████████░░▄▀░░██░░▄▀░░████████░░▄▀░░██░░▄▀░░█\n" +
                "█░░▄▀░░██░░▄▀░░███░░▄▀░░██░░▄▀░░███░░▄▀░░███░░░░▄▀░░░░████████████░░▄▀░░████░░▄▀░░████████░░▄▀░░████░░▄▀░░████████░░▄▀░░██░░▄▀░░████████░░▄▀░░██░░▄▀░░█\n" +
                "█░░▄▀░░░░░░▄▀░░░░█░░▄▀░░░░░░▄▀░░█░░░░▄▀░░░░█░░▄▀▄▀░░░░░░░░░░░░████░░▄▀░░░░░░░░▄▀░░█░░░░░░█░░▄▀░░░░░░░░▄▀░░█░░░░░░█░░▄▀░░░░▄▀▄▀░░█░░░░░░█░░▄▀░░░░▄▀▄▀░░█\n" +
                "█░░▄▀▄▀▄▀▄▀▄▀▄▀░░█░░▄▀▄▀▄▀▄▀▄▀░░█░░▄▀▄▀▄▀░░█░░▄▀▄▀▄▀▄▀▄▀▄▀▄▀░░████░░▄▀▄▀▄▀▄▀▄▀▄▀░░█░░▄▀░░█░░▄▀▄▀▄▀▄▀▄▀▄▀░░█░░▄▀░░█░░▄▀▄▀▄▀▄▀░░░░█░░▄▀░░█░░▄▀▄▀▄▀▄▀░░░░█\n" +
                "█░░░░░░░░░░░░░░░░█░░░░░░░░░░░░░░█░░░░░░░░░░█░░░░░░░░░░░░░░░░░░████░░░░░░░░░░░░░░░░█░░░░░░█░░░░░░░░░░░░░░░░█░░░░░░█░░░░░░░░░░░░███░░░░░░█░░░░░░░░░░░░███\n" +
                "███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████"+ANSI_RESET);
    }
}
