from flask import Flask, request, jsonify
from datetime import datetime
import socket
import argparse
import os
from gradientai import Gradient

app = Flask(__name__)

new_model_adapter = None

token = 'LXkZBp5a1nGCP16xM7QieYKNz2Ns0tOW'
workspace_id = 'b5f958d9-ffd4-41bb-a492-704114e02c8e_workspace'

os.environ['GRADIENT_ACCESS_TOKEN'] = token
os.environ['GRADIENT_WORKSPACE_ID'] = workspace_id

def prepareLlamaBot(name):
    gradient = Gradient()
    base_model = gradient.get_base_model(base_model_slug="llama2-7b-chat")
    global new_model_adapter
    new_model_adapter = base_model.create_model_adapter(name=name)

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
        response = new_model_adapter.complete(query=query, max_generated_token_count=511).generated_output
        return jsonify({'healthPlan': response}), 200
    except Exception as e:
        print(f"Error in /health_plan: {e}", flush=True)
        return jsonify({'error': 'Internal Server Error', 'details': str(e)}), 500

@app.route('/today_plan', methods=['POST'])
def today_plan():
    data = request.get_json()
    required_fields = ['age', 'gender', 'height', 'weight', 'health_condition', 'goal', 'dietary_preferences']
    if not all(field in data for field in required_fields):
        return jsonify({'error': 'Missing required user information'}), 400

    user_info = '\n'.join([f"{key.capitalize()}: {value}" for key, value in data.items()])
    query = f"[INST]Given the user's information:\n{user_info}\nGenerate today's meal plan and exercise plan.[/INST]"
    
    response = new_model_adapter.complete(query=query, max_generated_token_count=511).generated_experience
    return jsonify({'todayPlan': response}), 200

if __name__ == '__main__':
    default_name = f"Llama_{datetime.now().strftime('%Y%m%d_%H%M%S')}_{socket.gethostname()}"
    parser = argparse.ArgumentParser()
    parser.add_argument('--name', default=default_name, help='Specify a name for the model adapter')
    args = parser.parse_args()
    port_num = 5001
    print(f"Starting Llama bot with name {args.name}...\n This may take a while.")
    prepareLlamaBot(args.name)
    app.run(host='0.0.0.0', port=port_num)
