def run_code(code):
    exec(code, globals())
    print("run_code")


def call_android_bridge(params):
    android_bridge(params)
    print(f"call_android_bridge1")
