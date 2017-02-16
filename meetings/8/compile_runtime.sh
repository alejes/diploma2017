cp build/classes/main/kotlin/*   /home/alejes/Diplom/kotlin11M03/dist/kotlinc/lib/kotlin-runtime/kotlin/
cp src/main/java/kotlin/*   /home/alejes/Diplom/kotlin11M03/dist/kotlinc/lib/kotlin-runtime-sources/kotlin/
cd /home/alejes/Diplom/kotlin11M03/dist/kotlinc/lib/kotlin-runtime/
zip -r -u kotlin-runtime.jar .
cp kotlin-runtime.jar ../kotlin-runtime.jar


cd /home/alejes/Diplom/kotlin11M03/dist/kotlinc/lib/kotlin-runtime-sources/
zip -r -u kotlin-runtime-sources.jar kotlin
cp kotlin-runtime-sources.jar ../kotlin-runtime-sources.jar
