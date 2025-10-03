package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "password";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
         Scanner scanner = new Scanner(System.in);
         try {
             Connection connection = DriverManager.getConnection(url,username,password);
             Patient patient = new Patient(connection, scanner);
             Doctor doctor = new Doctor(connection);
             while(true) {
                 System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                 System.out.println("1].Add Patient");
                 System.out.println("2].View Patient");
                 System.out.println("3].View Doctors");
                 System.out.println("4].Book Appointment");
                 System.out.println("5].Exit");
                 System.out.println("Enter your choice");
                 int choice = scanner.nextInt();

                 switch (choice) {
                     case 1 :
                             patient.addPatient();
                             break;
                     case 2 :
                             patient.viewPatient();
                         break;

                     case 3 :
                              doctor.viewDoctor();
                         break;

                     case 4 :
                             bookAppointment(patient,doctor,connection,scanner);
                         break;

                     case 5 :
                         System.out.println("Thank you for using hospital system");
                         return;
                     default:
                         System.out.println("Enter valid choice!! ");
                 }

             }


         } catch (SQLException e) {
             e.printStackTrace();
         }

    }

    public static void bookAppointment( Patient patient, Doctor doctor, Connection connection ,Scanner scanner){
        System.out.println("Enter patient id: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor id: ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD)");
        String appoinmentDate = scanner.next();
        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId,appoinmentDate,connection)) {
                String appointmentQuery = "insert into appointments(patient_id, doctor_id,appointment_date) values (?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appoinmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected>0) {
                        System.out.println("Appointment Booked!");
                    }
                    else {
                        System.out.println("Failed to book appointment");
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("doctor not available on this date!!");
            }
        }
         else {
            System.out.println("Either patient or doctor doesnot exist!!");
        }
    }


    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate , Connection connection) {
    String query = "select count(*) from appointments where doctor_id = ? and appointment_date = ?";
    try {
         PreparedStatement preparedStatement = connection.prepareStatement(query);
         preparedStatement.setInt(1,doctorId);
         preparedStatement.setString(2,appointmentDate);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            if (count ==0) {
                return true;
            }
            else {
                return false;
            }
        }
    } catch (SQLException e) {
       e.printStackTrace();
    }
    return false;
    }
}
