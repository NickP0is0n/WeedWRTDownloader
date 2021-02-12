import net.lingala.zip4j.ZipFile
import java.io.File
import java.io.PrintWriter

fun makeInstallScript() {
    println("\n[*] Generating installation script...")
    val script = """
        echo "WeedWRT v${BuildInfo.version} (${BuildInfo.branch} branch)"
        echo "Gentleman kit for OpenWRT snapshots"
        echo "Created by NickP0is0n (github.com/NickP0is0n)"
        echo " "
        read  -n 1 -p "Press any key to start"
        echo "[*] Unboxing LuCI files to temp directory"
        mkdir -p "/tmp/luci-offline-packages"
        cp /luci-offline/*.ipk "/tmp/luci-offline-packages"
        echo "[*] Installing LuCI and dependencies"
        cd "/tmp/luci-offline-packages"
        opkg install *.ipk
        echo "[*] Removing temp files"
        rm -rf "/tmp/luci-offline-packages"
        echo "[*] Starting uhttpd service"
        /etc/init.d/uhttpd start
        /etc/init.d/uhttpd enable
        echo "Done"
    """.trimIndent()

    val output = File("${TEMP_DIRECTORY.absolutePath.removeSuffix("/luci-offline")}/weedwrt.sh")
    output.createNewFile()

    PrintWriter(output).use {
        it.print(script)
    }
    println("[*] Installation script generated.")
}

fun packageIntoArchive() {
    println("\n[*] Packaging files into archive...")
    val outputDirectory = File("output")
    outputDirectory.mkdir()
    val outputFile = File("output/weedwrt_generated.zip")
    val zipFile = ZipFile(outputFile)
    zipFile.addFile(File("tmp/weedwrt.sh"))
    zipFile.addFolder(TEMP_DIRECTORY)
}

fun removeTemporaryFiles() {
    println("[*] Cleaning up temp files...")
    File(TEMP_DIRECTORY.absolutePath.removeSuffix("/luci-offline")).deleteRecursively()
}