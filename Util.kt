import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

object Util {
    fun compareFiles(f1: String, f2: String, algorithm: String = "SHA-256"): Boolean {
        val md = MessageDigest.getInstance(algorithm)
        val d1 = md.digest(Files.readAllBytes(Path.of(f1)))
        val d2 = md.digest(Files.readAllBytes(Path.of(f2)))
        return d1.contentEquals(d2)


//        model.writeToFile("model1")
//        val read = Perceptron.readFromFile("model1")
//        read.writeToFile("model2")
//        println(compareFiles("model1", "model2"))

    }
}