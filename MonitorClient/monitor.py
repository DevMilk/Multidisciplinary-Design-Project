from flask import Flask
from flask import render_template
app = Flask(__name__)
import os
@app.route('/')
def hello_world():
    return render_template(os.path.join("Client.html"))