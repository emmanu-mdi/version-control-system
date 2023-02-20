package svcs

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

val help = mapOf(
    "config" to "Get and set a username.",
    "add" to "Add a file to the index.",
    "log" to "Show commit logs.",
    "commit" to "Save changes.",
    "checkout" to "Restore a file."
)
val vcsDir = File("vcs")
val indexFile = File("vcs/index.txt")
val configFile = File("vcs/config.txt")
val logFile = File("vcs/log.txt")
val commitsDir = File("vcs/commits")
val path = System.getProperty("user.dir")

fun copyFile(src: String, dest: String) {
    val file = File(src)
    if (file.exists()) {
        val sourcePath = Paths.get(src)
        val targetPath = Paths.get(dest)
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
    }
}

fun configArg(args: Array<String>) {
    when (args.size) {
        1 -> {
            if (configFile.readText().isBlank()) println("Please, tell me who you are.")
            else println("The username is ${configFile.readText()}.")
        }
        2 -> {
            configFile.writeText(args[1])
            println("The username is ${args[1]}.")
        }
    }
}

fun addArg(args: Array<String>) {
    when (args.size) {
        1 -> {
            if (indexFile.readText().isBlank()) {
                println("${help["add"]}")
            } else {
                println("Tracked files:")
                println(indexFile.readText())
            }
        }
        2 -> {
            val fileToTrackPath = File(args[1])
            if (fileToTrackPath.exists()) {
                if (indexFile.readText().isBlank()) {
                    indexFile.writeText(args[1])
                    println("The file '${args[1]}' is tracked.")
                } else {
                    indexFile.appendText("\n${args[1]}")
                    println("The file '${args[1]}' is tracked.")
                }
            } else {
                println("Can't find '${args[1]}'.")
            }
        }
    }
}

fun logArg(args: Array<String>) {
    when (args[0]) {
        "log" -> {
            if (logFile.readText().isBlank()) println("No commits yet.")
            else println(logFile.readText())
        }
    }
}

fun String.toMD5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.toHex()
}

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

fun commitArg(args: Array<String>){
    val indexPath = "${path}\\vcs\\index.txt"
    val hashPath = "${path}\\vcs\\hash.txt"
    if (!configFile.exists()) {
        configFile.createNewFile()
    }
    when (args.size) {
        1 -> {
            when (args[0]) {
                "commit" -> {
                    println("Message was not passed.")
                }
            }
        }

        2 -> {
            when (args[0]) {
                "commit" -> {
                    val hashCommitFile = File("${path}\\vcs\\hash.txt")
                    var isFirstCommit = false
                    if (!commitsDir.exists()) {
                        commitsDir.mkdirs()
                        isFirstCommit = true
                    }
                    val fromFile = File(path)
                    if (hashCommitFile.exists()) hashCommitFile.writeText("")
                    fromFile.walkTopDown().maxDepth(1).forEach { it ->
                        if ((it.isFile) && (it.extension == "txt")) {

                            val indexFileName = it.name
                            val textFileFrom = it.readText()
                            indexFile.forEachLine {
                                if (indexFileName == it) {
                                    if (hashCommitFile.exists()) {
                                        hashCommitFile.appendText("${indexFileName}\n${textFileFrom.toMD5()}\n")
                                    } else {
                                        hashCommitFile.createNewFile()
                                        hashCommitFile.writeText("${indexFileName}\n${textFileFrom.toMD5()}\n")
                                    }
                                }
                            }
                        }
                    }
                    val hashCommitFolderName = hashCommitFile.readText().toMD5()
                    File("${path}\\vcs\\commits\\$hashCommitFolderName").mkdirs()

                    if ((hashCommitFile.readText().toMD5() == indexFile.readText().toMD5()) && !isFirstCommit) {
                        println("Nothing to commit.")
                    } else {
                        fromFile.walkTopDown().maxDepth(1).forEach { it ->// create files in folder commits
                            if ((it.isFile) && (it.extension == "txt")) {
                                val fileThat = File("${path}\\vcs\\commits\\$hashCommitFolderName\\${it.name}")
                                val indexFileName = it.name
                                val textFileFrom = it.readText()
                                indexFile.forEachLine {
                                    if (indexFileName == it) {
                                        if (fileThat.exists()) {
                                            fileThat.writeText(textFileFrom)
                                        } else {
                                            fileThat.createNewFile()
                                            fileThat.writeText(textFileFrom)
                                        }
                                    }
                                }
                            }
                        }
                        val logFile = File("${path}\\vcs\\log.txt")
                        val logFileCopy = File("${path}\\vcs\\log_copy.txt")
                        if (logFile.exists()) {
                            logFileCopy.writeText("${logFile.readText()}\n")
                            logFile.writeText("commit ${hashCommitFolderName}\nAuthor: ${configFile.readText()}\n${args[1]}\n")
                            logFile.appendText("${logFileCopy.readText()}\n")
                        } else {
                            logFile.createNewFile()
                            logFile.writeText("commit ${hashCommitFolderName}\nAuthor: ${configFile.readText()}\n${args[1]}\n")
                        }
                        logFileCopy.delete()
                        println("Changes are committed.")
                        copyFile(hashPath, indexPath)
                    }
                }
            }
        }
    }
}

fun checkoutArg(args: Array<String>) {
    when (args.size) {
        1 -> {
            when (args[0]) {
                "checkout" -> {
                    println("Commit id was not passed.")
                }
            }
        }

        2 -> {
            when (args[0]) {
                "checkout" -> {
                    var isExistsCommitsID = false
                    val folderOfCommits = File("${path}\\vcs\\commits")
                    var choosingCommitFile = File("")
                    if (args[1] != "") {
                        folderOfCommits.walkTopDown().maxDepth(1).forEach {
                            if (args[1] == it.name) {
                                choosingCommitFile = it
                                isExistsCommitsID = true
                            }
                        }
                        if(isExistsCommitsID) {
                            choosingCommitFile.walkTopDown().maxDepth(1).forEach {
                                if ((it.isFile) && (it.extension == "txt")) {
                                    it.copyTo(File("${path}\\${it.name}"), overwrite = true)
                                }
                            }
                            println("Switched to commit ${choosingCommitFile.name}.")
                        } else {
                            println("Commit does not exist.")
                        }
                    } else {
                        println("Commit id was not passed.")
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val printHelp = """
        These are SVCS commands:
        config    ${help["config"]}
        add       ${help["add"]}
        log       ${help["log"]}
        commit    ${help["commit"]}
        checkout  ${help["checkout"]}
    """.trimIndent()

    vcsDir.mkdir()
    indexFile.createNewFile()
    configFile.createNewFile()
    logFile.createNewFile()

    if (args.isEmpty()) {
        print(printHelp)
    } else if (args[0] in args && args.size == 1) {
        when (args[0]) {
            "config" -> configArg(args)
            "add" -> addArg(args)
            "log" -> logArg(args)
            "commit" -> commitArg(args)
            "checkout" -> checkoutArg(args)
            "--help" -> println(printHelp)
            else -> println("'${args[0]}' is not a SVCS command.")
        }
    } else if (args[0] in args && args[1] in args) {
        when (args[0]) {
            "config" -> configArg(args)
            "add" -> addArg(args)
            "commit" -> commitArg(args)
            "checkout" -> checkoutArg(args)
        }
    }
}
