from flask import Flask, jsonify
import pandas as pd
import os

app = Flask(__name__)

# Define file paths for the two Excel files
FILE_1_PATH = "phase_data.xlsx"
FILE_2_PATH = "GE India 2024.xlsx"

@app.route('/phase-data', methods=['GET'])
def get_phase_data():
    """
    Endpoint to fetch data from 'phase_data.xlsx'
    """
    try:
        # Check if the file exists
        if not os.path.exists(FILE_1_PATH):
            return f"File not found: {FILE_1_PATH}", 404

        # Load the Excel file into a DataFrame
        df = pd.read_excel(FILE_1_PATH)
        
        # Convert DataFrame to a list of dictionaries
        data = df.to_dict(orient='records')
        return jsonify(data), 200
    
    except Exception as e:
        return str(e), 500

@app.route('/ge-india-data', methods=['GET'])
def get_ge_india_data():
    
    try:
        # Check if the file exists
        if not os.path.exists(FILE_2_PATH):
            return f"File not found: {FILE_2_PATH}", 404

        # Load the Excel file into a DataFrame
        df = pd.read_excel(FILE_2_PATH)
        
        # Convert DataFrame to a list of dictionaries
        data = df.to_dict(orient='records')
        return jsonify(data), 200
    
    except Exception as e:
        return str(e), 500

if __name__ == "__main__":
    app.run(debug=True)
