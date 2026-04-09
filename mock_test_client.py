import struct
import json
import subprocess
import sys
import os

def send_message(proc, message):
    # Encode message as JSON
    json_message = json.dumps(message).encode('utf-8')
    # Write message length as 4 bytes (little-endian)
    proc.stdin.write(struct.pack('@I', len(json_message)))
    # Write message
    proc.stdin.write(json_message)
    proc.stdin.flush()

def read_message(proc):
    # Read 4 bytes to get message length
    text_length_bytes = proc.stdout.read(4)
    if len(text_length_bytes) == 0:
        return None
    # Unpack little-endian 32-bit integer
    text_length = struct.unpack('@I', text_length_bytes)[0]
    # Read the JSON message
    json_message = proc.stdout.read(text_length).decode('utf-8')
    return json.loads(json_message)

if __name__ == "__main__":
    # Adjust paths as needed
    jar_path = r"d:\Price Tracker\backend\target\PriceTrackerEngine.jar"
    java_exe = r"D:\Tools\Java\jdk-17.0.10+7\bin\java.exe"
    
    # Start the Java process
    proc = subprocess.Popen([java_exe, '-jar', jar_path], 
                            stdin=subprocess.PIPE, 
                            stdout=subprocess.PIPE, 
                            stderr=subprocess.PIPE,
                            bufsize=0)

    print("--- Sending Mock Query: 'iPhone 15' ---")
    query = {"query": "iPhone 15"}
    
    send_message(proc, query)
    
    print("--- Waiting for Scrapers to finish... ---")
    response = read_message(proc)
    
    if response:
        print("\n--- SCRAPER RESULTS ---")
        print(json.dumps(response, indent=4))
    else:
        print("\n--- FAILED: No response from host ---")
    
    # Check for errors in stderr
    stderr_output = proc.stderr.read()
    if stderr_output:
        print("\n--- SERVER ERRORS ---")
        print(stderr_output.decode('utf-8'))

    proc.terminate()
