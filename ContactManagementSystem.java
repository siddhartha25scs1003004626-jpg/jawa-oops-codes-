import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class ContactManagementSystem extends JFrame {
    private List<Contact> contacts;
    private DefaultListModel<Contact> listModel;
    private JList<Contact> contactList;
    private JTextField searchField;
    private JTextArea duplicateArea;
    private JLabel countLabel;
    private final String DATA_FILE = "contacts.txt";
    
    // Colors
    private final Color PRIMARY = new Color(139, 92, 246);
    private final Color SECONDARY = new Color(236, 72, 153);
    private final Color BG_LIGHT = new Color(250, 245, 255);
    private final Color WARNING = new Color(251, 191, 36);
    
    public ContactManagementSystem() {
        contacts = new ArrayList<>();
        listModel = new DefaultListModel<>();
        
        setTitle("Contact Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        loadContacts();
        initComponents();
        updateContactList();
        checkDuplicates();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BG_LIGHT);
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setBackground(BG_LIGHT);
        
        // Left Panel - Contact List
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Right Panel - Contact Details
        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("📇 Contact Manager");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY);
        
        countLabel = new JLabel(contacts.size() + " contacts");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        countLabel.setForeground(Color.GRAY);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(countLabel, BorderLayout.CENTER);
        
        // Search Bar
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        
        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateContactList();
            }
        });
        
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        // Add Button
        JButton addButton = createStyledButton("➕ Add Contact", PRIMARY);
        addButton.addActionListener(e -> showAddContactDialog());
        
        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Duplicate Warning
        duplicateArea = new JTextArea();
        duplicateArea.setEditable(false);
        duplicateArea.setBackground(new Color(254, 252, 232));
        duplicateArea.setForeground(new Color(146, 64, 14));
        duplicateArea.setFont(new Font("Arial", Font.BOLD, 12));
        duplicateArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WARNING, 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        duplicateArea.setVisible(false);
        
        // Contact List
        contactList = new JList<>(listModel);
        contactList.setCellRenderer(new ContactListRenderer());
        contactList.setFont(new Font("Arial", Font.PLAIN, 14));
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(contactList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        panel.add(duplicateArea, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel infoLabel = new JLabel("<html><center><br><br>📋<br><br>" +
            "Select a contact to view details<br>or add a new contact</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(infoLabel, BorderLayout.CENTER);
        
        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && contactList.getSelectedValue() != null) {
                showContactDetails(panel);
            }
        });
        
        return panel;
    }
    
    private void showContactDetails(JPanel panel) {
        Contact contact = contactList.getSelectedValue();
        if (contact == null) return;
        
        panel.removeAll();
        panel.setLayout(new BorderLayout(0, 20));
        
        // Contact Card
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Avatar
        JLabel avatar = new JLabel(String.valueOf(contact.name.charAt(0)).toUpperCase());
        avatar.setFont(new Font("Arial", Font.BOLD, 48));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(PRIMARY);
        avatar.setPreferredSize(new Dimension(80, 80));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Name
        JLabel nameLabel = new JLabel(contact.name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        // Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        addDetailRow(detailsPanel, "📞", "Phone", contact.phone, isDuplicate(contact.phone));
        if (!contact.email.isEmpty()) {
            addDetailRow(detailsPanel, "📧", "Email", contact.email, false);
        }
        if (!contact.address.isEmpty()) {
            addDetailRow(detailsPanel, "📍", "Address", contact.address, false);
        }
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton editButton = createStyledButton("✏️ Edit", new Color(59, 130, 246));
        editButton.addActionListener(e -> showEditContactDialog(contact));
        
        JButton deleteButton = createStyledButton("🗑️ Delete", new Color(239, 68, 68));
        deleteButton.addActionListener(e -> deleteContact(contact));
        
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        cardPanel.add(avatar);
        cardPanel.add(nameLabel);
        cardPanel.add(detailsPanel);
        cardPanel.add(buttonPanel);
        
        JScrollPane scrollPane = new JScrollPane(cardPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        panel.revalidate();
        panel.repaint();
    }
    
    private void addDetailRow(JPanel panel, String icon, String label, String value, boolean isDup) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.WHITE);
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Arial", Font.BOLD, 12));
        labelText.setForeground(Color.GRAY);
        
        JLabel valueText = new JLabel(value + (isDup ? " ⚠️ DUPLICATE" : ""));
        valueText.setFont(new Font("Arial", Font.PLAIN, 14));
        if (isDup) valueText.setForeground(WARNING);
        
        textPanel.add(labelText, BorderLayout.NORTH);
        textPanel.add(valueText, BorderLayout.CENTER);
        
        row.add(iconLabel, BorderLayout.WEST);
        row.add(textPanel, BorderLayout.CENTER);
        
        panel.add(row);
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void showAddContactDialog() {
        showContactDialog(null);
    }
    
    private void showEditContactDialog(Contact contact) {
        showContactDialog(contact);
    }
    
    private void showContactDialog(Contact existingContact) {
        JDialog dialog = new JDialog(this, existingContact == null ? "Add Contact" : "Edit Contact", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField nameField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        
        if (existingContact != null) {
            nameField.setText(existingContact.name);
            phoneField.setText(existingContact.phone);
            emailField.setText(existingContact.email);
            addressArea.setText(existingContact.address);
        }
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name: *"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Phone: *"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(addressArea), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton(existingContact == null ? "Save" : "Update");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and Phone are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (existingContact == null) {
                Contact newContact = new Contact(name, phone, emailField.getText().trim(), addressArea.getText().trim());
                contacts.add(newContact);
            } else {
                existingContact.name = name;
                existingContact.phone = phone;
                existingContact.email = emailField.getText().trim();
                existingContact.address = addressArea.getText().trim();
            }
            
            saveContacts();
            updateContactList();
            checkDuplicates();
            updateContactCount();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void deleteContact(Contact contact) {
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete " + contact.name + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            contacts.remove(contact);
            saveContacts();
            updateContactList();
            checkDuplicates();
            updateContactCount();
        }
    }
    
    private void updateContactList() {
        listModel.clear();
        String search = searchField.getText().toLowerCase();
        
        contacts.stream()
            .filter(c -> fuzzyMatch(c.name, search) || 
                        fuzzyMatch(c.phone, search) || 
                        fuzzyMatch(c.email, search))
            .sorted((a, b) -> a.name.compareToIgnoreCase(b.name))
            .forEach(listModel::addElement);
    }
    
    private void updateContactCount() {
        countLabel.setText(contacts.size() + " contacts");
    }
    
    private boolean fuzzyMatch(String str, String pattern) {
        if (pattern.isEmpty()) return true;
        
        str = str.toLowerCase();
        pattern = pattern.toLowerCase();
        
        if (str.contains(pattern)) return true;
        
        int pIdx = 0;
        for (int i = 0; i < str.length() && pIdx < pattern.length(); i++) {
            if (str.charAt(i) == pattern.charAt(pIdx)) {
                pIdx++;
            }
        }
        return pIdx == pattern.length();
    }
    
    private void checkDuplicates() {
        Map<String, Integer> phoneCount = new HashMap<>();
        Set<String> duplicatePhones = new HashSet<>();
        
        for (Contact c : contacts) {
            String phone = c.phone.replaceAll("\\D", "");
            phoneCount.put(phone, phoneCount.getOrDefault(phone, 0) + 1);
            if (phoneCount.get(phone) > 1) {
                duplicatePhones.add(phone);
            }
        }
        
        if (!duplicatePhones.isEmpty()) {
            duplicateArea.setText("⚠️ DUPLICATE WARNING: " + duplicatePhones.size() + 
                " phone number(s) appear multiple times!");
            duplicateArea.setVisible(true);
        } else {
            duplicateArea.setVisible(false);
        }
    }
    
    private boolean isDuplicate(String phone) {
        String cleaned = phone.replaceAll("\\D", "");
        long count = contacts.stream()
            .filter(c -> c.phone.replaceAll("\\D", "").equals(cleaned))
            .count();
        return count > 1;
    }
    
    private void saveContacts() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Contact c : contacts) {
                // Format: name|phone|email|address
                writer.println(escapeDelimiters(c.name) + "|" + 
                              escapeDelimiters(c.phone) + "|" + 
                              escapeDelimiters(c.email) + "|" + 
                              escapeDelimiters(c.address));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving contacts: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadContacts() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 4) {
                    contacts.add(new Contact(
                        unescapeDelimiters(parts[0]),
                        unescapeDelimiters(parts[1]),
                        unescapeDelimiters(parts[2]),
                        unescapeDelimiters(parts[3])
                    ));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading contacts: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String escapeDelimiters(String str) {
        return str.replace("|", "\\|").replace("\n", "\\n");
    }
    
    private String unescapeDelimiters(String str) {
        return str.replace("\\|", "|").replace("\\n", "\n");
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ContactManagementSystem().setVisible(true);
        });
    }
}

// Contact Model
class Contact {
    String name;
    String phone;
    String email;
    String address;
    
    public Contact(String name, String phone, String email, String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
    
    @Override
    public String toString() {
        return name + " - " + phone;
    }
}

// Custom List Renderer
class ContactListRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {
        
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof Contact) {
            Contact contact = (Contact) value;
            label.setText("<html><b>" + contact.name + "</b><br>" + 
                         "<font color='gray'>" + contact.phone + "</font></html>");
            label.setBorder(new EmptyBorder(10, 10, 10, 10));
        }
        
        return label;
    }
}
