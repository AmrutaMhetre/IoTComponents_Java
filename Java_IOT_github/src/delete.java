import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class delete {

    private JComboBox<Integer> groupNumberComboBox;
    private DefaultTableModel tableModel;
    private JTable groupDetailsTable;

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new delete());
    }

    delete() {
        JFrame frame = new JFrame("Delete Components");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Group Number ComboBox
        JPanel comboBoxPanel = new JPanel();
        comboBoxPanel.add(new JLabel("Select Group Number:"));
        groupNumberComboBox = new JComboBox<>();
        loadGroupNumbers();
        comboBoxPanel.add(groupNumberComboBox);

        // Group Details Table
        tableModel = new DefaultTableModel();
        createTableModel();
        groupDetailsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(groupDetailsTable);

        // Add components to the main panel
        panel.add(comboBoxPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        frame.getContentPane().add(panel);
        frame.setVisible(true);

        // ActionListener for the View Details button
        groupNumberComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer selectedGroupNumber = (Integer) groupNumberComboBox.getSelectedItem();
                if (selectedGroupNumber != null) {
                    // Load and display details for the selected group number
                    loadAndDisplayGroupDetails(selectedGroupNumber);
                }
            }
        });

        // Add ActionListener for the Delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected row and column
                int selectedRow = groupDetailsTable.getSelectedRow();
                if (selectedRow != -1) {
                    int prn = (int) tableModel.getValueAt(selectedRow, 0);
                    Integer selectedGroupNumber = (Integer) groupNumberComboBox.getSelectedItem();
                    deleteRow(selectedGroupNumber.toString(), prn);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a record to delete.");
                }
            }
        });

        panel.add(deleteButton, BorderLayout.SOUTH);
    }

    private void loadGroupNumbers() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");
            String query = "SELECT DISTINCT CAST(G_No AS CHAR) AS G_No FROM group5";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String groupNumberString = resultSet.getString("G_No");
                if (groupNumberString != null && !groupNumberString.isEmpty()) {
                    groupNumberComboBox.addItem(Integer.parseInt(groupNumberString));
                }
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableModel() {
        tableModel.addColumn("PRN");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Class");
        tableModel.addColumn("Issue Date");
        tableModel.addColumn("Mobile");
        tableModel.addColumn("Email");
        tableModel.addColumn("Return Date");
        tableModel.addColumn("Component");
        tableModel.addColumn("Quantity");
        // No need for the "Delete" column here
    }

    private void loadAndDisplayGroupDetails(int selectedGroupNumber) {
        // Clear existing data in the table
        tableModel.setRowCount(0);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");
            String query = "SELECT * FROM group5 WHERE G_No=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, selectedGroupNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int prn = resultSet.getInt("PRN");
                String firstName = resultSet.getString("f_name");
                String lastName = resultSet.getString("l_name");
                String className = resultSet.getString("className");
                String issueDate = resultSet.getString("Issue_Date");
                String mobile = resultSet.getString("mo_no");
                String email = resultSet.getString("email");
                String returnDate = resultSet.getString("Return_Date");
                String componentName = resultSet.getString("C_name");
                int quantity = resultSet.getInt("Quantity");

                // Add row to the table model
                tableModel.addRow(new Object[]{prn, firstName, lastName, className, issueDate, mobile, email, returnDate, componentName, quantity});
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteRow(String groupNumber, int prn) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");
            String query = "DELETE FROM group5 WHERE G_No = ? AND PRN=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, groupNumber);
            preparedStatement.setInt(2, prn);
            preparedStatement.executeUpdate();

            // Refresh the table after deletion
            loadAndDisplayGroupDetails(Integer.parseInt(groupNumber));

            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
