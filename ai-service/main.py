from fastapi import FastAPI

app = FastAPI(
    title="Gharsa AI Service",
    description="Service d'intelligence artificielle pour le projet Gharsa",
    version="0.1.0"
)

@app.get("/health")
def health_check():
    return {
        "status": "ok",
        "service": "Gharsa AI Service"
    }
