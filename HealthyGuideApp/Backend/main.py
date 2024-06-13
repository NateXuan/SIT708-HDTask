from flask import Flask, request, jsonify
from datetime import datetime
import socket
import argparse
import os
from gradientai import Gradient

app = Flask(__name__)
base_model = None
token = 'xMhnlS6flGCDrHFgAlSXFpuPDroCMOgq'
workspace_id = 'a94488d7-458c-495a-bb1d-a001b194b190_workspace'

os.environ['GRADIENT_ACCESS_TOKEN'] = token
os.environ['GRADIENT_WORKSPACE_ID'] = workspace_id

def prepareLlamaBot():
    global base_model
    gradient = Gradient()
    base_model = gradient.get_base_model(base_model_slug="llama3-8b-chat")

@app.route('/')
def index():
    return "Welcome to the Flask API!"

@app.route('/health_plan', methods=['POST'])
def health_plan():
    try:
        data = request.get_json()
        print(f"Received data: {data}")
        required_fields = ['age', 'gender', 'height', 'weight', 'health_condition', 'goal', 'dietary_preferences']
        if not all(field in data for field in required_fields):
            missing_fields = [field for field in required_fields if field not in data]
            print(f"Missing fields: {missing_fields}")
            return jsonify({'error': 'Missing required user information', 'missing_fields': missing_fields}), 400

        user_info = '\n'.join([f"{key.capitalize()}: {value}" for key, value in data.items()])
        query = f"[INST]Given the user information:\n{user_info}\nGenerate a summary of the user's health and fitness plans.[/INST]"
        response = base_model.complete(query=query, max_generated_token_count=511).generated_output
        return jsonify({'healthPlan': response}), 200
    except Exception as e:
        print(f"Error in /health_plan: {e}", flush=True)
        return jsonify({'error': 'Internal Server Error', 'details': str(e)}), 500

@app.route('/today_plan', methods=['POST'])
def today_plan():
    data = request.get_json()
    print(f"Received data: {data}")
    required_fields = ['age', 'gender', 'height', 'weight', 'health_condition', 'goal', 'dietary_preferences']
    if not all(field in data for field in required_fields):
        return jsonify({'error': 'Missing required user information'}), 400

    user_info = '\n'.join([f"{key.capitalize()}: {value}" for key, value in data.items()])
    query = f"[INST]Given the user's information:\n{user_info}\nGenerate today's meal plan and exercise plan.[/INST]"
    
    response = base_model.complete(query=query, max_generated_token_count=511).generated_output
    return jsonify({'todayPlan': response}), 200

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--port', type=int, default=5001, help='Specify the port number')
    args = parser.parse_args()

    port_num = args.port
    print("Starting Llama bot...\n This may take a while.")
    prepareLlamaBot()
    print(f"App running on port {port_num}")
    app.run(port=port_num)
