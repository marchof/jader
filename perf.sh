mvn compile
echo "Running shader performance test..."
java --enable-preview -cp target/classes:target/test-classes jader.ShaderPerformance

