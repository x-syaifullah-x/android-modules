import firebase_admin.auth
import firebase_functions.https_fn
from flask import Flask, request, jsonify
from functions_wrapper import entrypoint
import firebase_admin
import firebase_functions

# cert = firebase_admin.credentials.Certificate("../aa.json")
firebase_admin.initialize_app()

app_flask = Flask(__name__)

# curl -i -X POST http://127.0.0.1:5001/x-x-x-projects/us-central1/api/auth/check -H 'Content-Type: application/json' -d '{"type": "phone", "phone": "+628123456789"}'
@app_flask.route("/auth/check", methods=["POST"])
def world():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"error": "No JSON data provided"}), 400
        type = data["type"]
        if (type == "phone"):
            phone_number = data["phone"]
            result = firebase_admin.auth.get_user_by_phone_number(phone_number=phone_number)
            return jsonify({
                "uid": result.uid,
                "email": result.email,
                "phone_number": result.phone_number,
                "display_name": result.display_name,
                "photo_url": result.photo_url,
                "disabled": result.disabled,
                "provider_id": result.provider_id,
                "created_at": result.user_metadata.creation_timestamp,
                "last_login_at": result.user_metadata.last_sign_in_timestamp
            }), 200
        elif (type == "google"):
            id_token = data["id_token"]
            if not id_token:
                return jsonify({"error": "ID token is required"}), 400
            decoded_token = firebase_admin.auth.verify_id_token(id_token)
            uid = decoded_token["uid"]
            result = firebase_admin.auth.get_user(uid=uid)
            return jsonify({
                "uid": result.uid,
                "email": result.email,
                "phone_number": result.phone_number,
                "display_name": result.display_name,
                "photo_url": result.photo_url,
                "disabled": result.disabled,
                "provider_id": result.provider_id,
                "created_at": result.user_metadata.creation_timestamp,
                "last_login_at": result.user_metadata.last_sign_in_timestamp
            }), 200
    except firebase_admin.auth.UserNotFoundError as e:
        return jsonify({"error": "User not found"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    return "Hello World!"

# http://127.0.0.1:5001/x-x-x-projects/us-central1/api/{route}
@firebase_functions.https_fn.on_request()
def api(request):
    return entrypoint(app_flask, request)
