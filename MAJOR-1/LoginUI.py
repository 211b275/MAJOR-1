from PyQt5 import QtCore, QtGui, QtWidgets
import pandas as pd
import matplotlib.pyplot as plt
import os


class Ui_Form(object):
    def setupUi(self, Form):
        Form.setObjectName("Form")
        Form.resize(1121, 853)
        Form.setWindowFlags(QtCore.Qt.FramelessWindowHint)
        Form.setAttribute(QtCore.Qt.WA_TranslucentBackground)

        self.widget = QtWidgets.QWidget(Form)
        self.widget.setGeometry(QtCore.QRect(29, 29, 1051, 791))
        self.widget.setObjectName("widget")

        self.label = QtWidgets.QLabel(self.widget)
        self.label.setGeometry(QtCore.QRect(140, 120, 751, 541))
        self.label.setStyleSheet(
            "background-color: qconicalgradient(cx:0.5, cy:0.5, angle:0, stop:0 rgba(16, 209, 77, 204), stop:1 rgba(255, 255, 255, 255));\n"
            "border-radius: 100px;"
        )
        self.label.setText("")

        self.lineEdit = QtWidgets.QLineEdit(self.widget)
        self.lineEdit.setGeometry(QtCore.QRect(380, 290, 250, 51))
        self.lineEdit.setPlaceholderText("Enter Candidate State")

        self.generatePieButton = QtWidgets.QPushButton(self.widget)
        self.generatePieButton.setGeometry(QtCore.QRect(420, 430, 150, 55))
        self.generatePieButton.setText("Spawn")

        self.retranslateUi(Form)
        QtCore.QMetaObject.connectSlotsByName(Form)

    def retranslateUi(self, Form):
        _translate = QtCore.QCoreApplication.translate
        Form.setWindowTitle(_translate("Form", "Election Data Viewer"))


class MainWindow(QtWidgets.QMainWindow):
    def __init__(self):
        super(MainWindow, self).__init__()
        self.ui = Ui_Form()
        self.ui.setupUi(self)

        # Connect button to method
        self.ui.generatePieButton.clicked.connect(self.generate_pie_chart)

    def generate_pie_chart(self):
        state = self.ui.lineEdit.text().strip()
        if not state:
            QtWidgets.QMessageBox.warning(self, "Input Error", "Please enter a state name.")
            return

        file_path = "phase_data.xlsx"  # Ensure this file is in the same directory as the script
        if not os.path.exists(file_path):
            QtWidgets.QMessageBox.critical(self, "File Error", "The file 'phase_data.xlsx' is missing.")
            return

        try:
            # Load the data
            df = pd.read_excel(file_path)

            # Filter the data by the entered state
            state_data = df[df["State"].str.strip().str.lower() == state.lower()]
            if state_data.empty:
                QtWidgets.QMessageBox.warning(self, "Data Error", f"No data found for state: {state}")
                return

            # Use each proportion individually
            chart_data = state_data["**Poll (%)"]
            labels = state_data["State"]  # Assuming each row corresponds to a constituency

            # Plot the pie chart
            plt.figure(figsize=(8, 8))
            plt.pie(
                chart_data,
                labels=labels,
                autopct='%1.1f%%',
                startangle=140,
                colors=plt.cm.tab20.colors
            )
            plt.title(f"Poll Distribution in {state}")
            plt.tight_layout()
            plt.show()

        except Exception as e:
            QtWidgets.QMessageBox.critical(self, "Error", f"An error occurred: {e}")


if __name__ == "__main__":
    import sys

    app = QtWidgets.QApplication(sys.argv)
    window = MainWindow()
    window.show()
    sys.exit(app.exec_())
