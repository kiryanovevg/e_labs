package main

import java.io.*

const val TAGin = "Ввод данных"
const val TAGout = "Вывод данных"
const val TAGerror = "Ошибка"

const val inputFileName = "input.txt"
const val outputFileName = "output.txt"

const val generationPath = "generations"
const val transformationPath = "transformations"

fun tryGet(name: String): Int {
    return tryGetFile(name)
}

private val reader = BufferedReader(InputStreamReader(FileInputStream(inputFileName)))
private fun tryGetFile(name: String): Int {
    Print(TAGin, "Введите $name")
    val can = false
    while (!can) {
        try {
            val value = reader.readLine().split(":")[1].toInt()
            if (value > 0) {
                Println("$value")
                return value
            } else Print(TAGin, "Введено неверное значение")
        } catch (e: Exception) {
            Print(TAGin, "Введено неверное значение")
        }
    }
    return 0
}

private fun tryGetConsole(name: String): Int {
    Print(TAGin, "Введите $name")
    val can = false
    while (!can) {
        try {
            val value = readLine()?.toInt() ?: 0
            if (value > 0) {
                return value
            } else Print(TAGin, "Введено неверное значение")
        } catch (e: Exception) {
            Print(TAGin, "Введено неверное значение")
        }
    }
    return 0
}

private const val divider = "---------------------"

interface Printer {
    fun Print(string: String)
    fun Println(string: String)
    fun Println()
    fun PrintDiv()
}

private val consolePrinter = object : Printer {
    override fun Print(string: String) {
        print(string)
    }

    override fun Println(string: String) {
        println(string)
    }

    override fun Println() {
        println()
    }

    override fun PrintDiv() {
        Println(divider)
    }
}

private val filePrinter = object : Printer {
    var outputFile: String = outputFileName

    override fun Print(string: String) {
        FileWriter(outputFile, true).use { out ->
            out.append(string)
            out.flush()
        }
    }

    override fun Println(string: String) {
        FileWriter(outputFile, true).use { out ->
            out.append("$string\n")
            out.flush()
        }
    }

    override fun Println() {
        FileWriter(outputFile, true).use { out ->
            out.append("\n")
            out.flush()
        }
    }

    override fun PrintDiv() {
        Println(divider)
    }
}

val MainPrinter = consolePrinter

fun Print(tag: String, message: String) {
    System.out.println("$tag : $message")
}

fun Print(string: String) {
    MainPrinter.Print(string)
}

fun Println(string: String) {
    MainPrinter.Println(string)
}

fun Println() {
    MainPrinter.Println()
}

fun PrintDiv() {
    MainPrinter.PrintDiv()
}

fun printInFile(path: String, name: String, append: Boolean, print: (Printer) -> Unit) {
    val dir = File(path)
    if (!dir.exists()) dir.mkdirs()

    val file = File("${dir.absolutePath}${File.separator}$name.txt")
    if (!append && file.exists()) {
        file.delete()
        file.createNewFile()
    }

    filePrinter.outputFile = file.absolutePath
    print.invoke(filePrinter)
    filePrinter.outputFile = outputFileName
}