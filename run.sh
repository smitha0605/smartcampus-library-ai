#!/bin/bash
# SmartCampus Library AI — Run Script
# Usage: ./run.sh

echo "🔥 SmartCampus Library AI — IIIT-B Edition"
echo "============================================"

# Check Java 17+
if ! java -version 2>&1 | grep -q "version \"1[78]\|version \"2[0-9]"; then
    echo "⚠️  Java 17+ required. Current version:"
    java -version
fi

# Build if JAR doesn't exist
if [ ! -f "target/smartcampus-library-ai.jar" ]; then
    echo "📦 Building project..."
    mvn clean package -q
fi

# Run
echo "🚀 Launching..."
java -Dfile.encoding=UTF-8 -jar target/smartcampus-library-ai.jar
