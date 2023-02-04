package svcs

import java.io.File

fun main(args: Array<String>) {
    val help = mapOf(
        "config" to "Get and set a username.",
        "add" to "Add a file to the index.",
        "log" to "Show commit logs.",
        "commit" to "Save changes.",
        "checkout" to "Restore a file."
    )
    val printHelp = """
        These are SVCS commands:
        config    ${help["config"]}
        add       ${help["add"]}
        log       ${help["log"]}
        commit    ${help["commit"]}
        checkout  ${help["checkout"]}
    """.trimIndent()

    val vcsDir = File("vcs")
    vcsDir.mkdir()
    val indexFile = File("vcs/index.txt")
    indexFile.createNewFile()
    val configFile = File("vcs/config.txt")
    configFile.createNewFile()
    val fileToTrackPath: File

    if (args.isEmpty()) {
        print(printHelp)
    } else if (args[0] in args && args.size == 1) {
        when (args[0]) {
            "config" -> {
                if (configFile.readText().isBlank()) println("Please, tell me who you are.")
                else println("The username is ${configFile.readText()}.")
            }
            "add" -> {
                if (indexFile.readText().isBlank()) {
                    println("${help["add"]}")
                } else {
                    println("Tracked files:")
                    println(indexFile.readText())
                }
            }
            "log" -> println("${help["log"]}")
            "commit" -> println("${help["commit"]}")
            "checkout" -> println("${help["checkout"]}")
            "--help" -> println(printHelp)
            else -> println("'${args[0]}' is not a SVCS command.")
        }
    } else if (args[0] in args && args[1] in args) {
        when (args[0]) {
            "config" -> {
                configFile.writeText(args[1])
                println("The username is ${args[1]}.")
            }
            "add" -> {
                fileToTrackPath = File(args[1])
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
}