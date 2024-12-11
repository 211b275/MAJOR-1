import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.imageio.ImageIO;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class MainUI 
{
    public static void main(String[] args) 
    {
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) 
        {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Main UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton phaseDataButton = new JButton(new ImageIcon(loadIcon("draw.png")));
        JButton geIndiaButton = new JButton(new ImageIcon(loadIcon("arrow (1).png")));
        JButton generateButton = new JButton(new ImageIcon(loadIcon("arrow.png")));

        phaseDataButton.setToolTipText("View Phase Data");
        geIndiaButton.setToolTipText("View GE India Data");
        generateButton.setToolTipText("Analyze Data");

        buttonPanel.add(phaseDataButton);
        buttonPanel.add(geIndiaButton);
        buttonPanel.add(generateButton);

        frame.add(buttonPanel, BorderLayout.NORTH);

        // Create background panel with scrolling images
        MotionCaptureBackgroundPanel backgroundPanel = new MotionCaptureBackgroundPanel(new String[]
        {
            "resources/Images/Votes.jpg", // Use relative paths
            "resources/Images/Analyse.jpg" // Use relative paths
        });
        frame.add(backgroundPanel, BorderLayout.CENTER);

        JTable dataTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        frame.add(scrollPane, BorderLayout.SOUTH);

        phaseDataButton.addActionListener(e -> 
        {
            String apiUrl = "http://127.0.0.1:5000/phase-data";
            fetchDataAndDisplay(apiUrl, dataTable);
        });

        geIndiaButton.addActionListener(e -> 
        {
            String apiUrl = "http://127.0.0.1:5000/ge-india-data";
            fetchDataAndDisplay(apiUrl, dataTable);
        });

        generateButton.addActionListener(e -> 
        {
            try 
            {
                String pythonScriptPath = "C:\\Users\\Acer\\OneDrive\\Desktop\\MAJOR-1\\LoginUI.py"; 
                String pythonPath = "python";
                ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, pythonScriptPath);
                processBuilder.start();
            } 
            catch (Exception ex) 
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error launching Python UI: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Timer to update background images
        Timer timer = new Timer(30, e -> backgroundPanel.moveBackground());
        timer.start();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static Image loadIcon(String iconName) 
    {
        try 
        {
            String iconPath = "resources/Images/" + iconName;
            System.out.println("Loading icon from: " + iconPath); // Debugging line
            File iconFile = new File(iconPath);
            if (iconFile.exists()) 
            {
                BufferedImage image = ImageIO.read(iconFile);
                return image.getScaledInstance(52, 32, Image.SCALE_SMOOTH);
            } 
            else 
            {
                System.out.println("Icon file not found: " + iconPath); // Debugging line
                return new ImageIcon("resources/Images/speech.jpg").getImage(); // Use an image from the Images folder
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return null;
        }
    }
    @SuppressWarnings("deprecation")
    private static void fetchDataAndDisplay(String apiUrl, JTable table) 
    {
        try 
        {
            // Fetch data from the API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) 
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) 
                {
                    response.append(line);
                }
                reader.close();
                JSONArray jsonArray = new JSONArray(response.toString());
                displayDataInTable(jsonArray, table);
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "Failed to fetch data. Response code: " + responseCode,"Error", JOptionPane.ERROR_MESSAGE);
            }
        } 
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(null, "Error fetching data: " + e.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    @SuppressWarnings("unchecked")
    private static void displayDataInTable(JSONArray jsonArray, JTable table) {
        try {
            String[] columnNames = (String[]) jsonArray.getJSONObject(0).keySet().toArray(new String[0]);  // Adjusted to handle Object to String[] conversion
            Object[][] data = new Object[jsonArray.length()][columnNames.length];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                for (int j = 0; j < columnNames.length; j++) {
                    data[i][j] = jsonObject.get(columnNames[j]);
                }
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
            table.setModel(tableModel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error parsing JSON: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class MotionCaptureBackgroundPanel extends JPanel {
        private BufferedImage[] backgroundImages;
        private int xOffset = 0;
        private int scrollSpeed = 6;

        public MotionCaptureBackgroundPanel(String[] imagePaths) {
            backgroundImages = new BufferedImage[imagePaths.length];

            try {
                for (int i = 0; i < imagePaths.length; i++) {
                    backgroundImages[i] = ImageIO.read(new File(imagePaths[i]));
                }

                Timer timer = new Timer(19, e -> moveBackground());
                timer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void moveBackground() {
            xOffset -= scrollSpeed;

            if (xOffset <= -getWidth()) {
                xOffset = 0;
            }

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int i = 0; i < backgroundImages.length; i++) {
                g.drawImage(backgroundImages[i], xOffset + i * getWidth(), 0, getWidth(), getHeight(), this);
            }
        }
    }
}
