import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class PackageParser (packageFile: File) {
    val packageList: List<Package>

    init {
        val reader = BufferedReader(FileReader(packageFile))
        var str: String? = ""

        var currentPackageName = ""
        var currentPackageFileName = ""

        val parsedPackages = mutableListOf<Package>()
        while ((reader.readLine().also { str = it }) != null) {
            if (str!!.contains("Package:")) {
                currentPackageName = str!!.removePrefix("Package: ")
            }
            else if (str!!.contains("Filename:")) {
                currentPackageFileName = str!!.removePrefix("Filename: ")
                parsedPackages.add(Package(currentPackageName, currentPackageFileName))
            }
        }
        packageList = parsedPackages
    }
}