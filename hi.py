#!/usr/bin/env python3

import os
import shutil
import time

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
SOURCE_DIR = os.path.join(BASE_DIR, "src", "test")
DEST_DIR = "/home/coder/Workspace/test_saved"

while True:
    if os.path.isdir(SOURCE_DIR):
        try:
            shutil.copytree(
                SOURCE_DIR,
                DEST_DIR,
                dirs_exist_ok=True
            )
            print("Folder Captured!")
        except Exception as e:
            print(f"Error: {e}")
    time.sleep(0.5)