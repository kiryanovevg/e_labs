package main

import java.lang.IndexOutOfBoundsException
import java.util.concurrent.ThreadLocalRandom

/**
 * Однородная матрица
 */
fun makeHomogeneous(N: Int, M: Int, t1: Int, t2: Int): Array<Array<Int>> {
    val firstRow = Array(M) {
        ThreadLocalRandom.current().nextInt(t1, t2)
    }

    return Array(N) {
        firstRow.clone()
    }
}

/**
 * Печать матрицы в виде
 *  1 1 1
 *  2 2 2
 *  3 3 3
 *  4 4 4
 */
fun Array<Array<Int>>.print() {
    for (j in 0 until this[0].size) {
        for (i in 0 until this.size) {
            Print("${this[i][j]} \t")
        }
        Println()
    }
}
/**
 * Генерация первого покления
 */
fun generateFirstG(Z: Int, M: Int): ArrayList<Array<Int>> {
    return arrayListOf<Array<Int>>().apply {
        repeat(Z) {
            this.add(Array(M) {
                ThreadLocalRandom.current().nextInt(0, 255)
            })
        }
    }
}

/**
 * Печать матрицы в виде
 *  1 1 1 1 1
 *  2 2 2 2 2
 *  3 3 3 3 3
 */
fun ArrayList<Array<Int>>.printGenerate(NM: Array<Array<Int>>, current: Int) {
    printInFile(generationPath, "Generation#${current + 1}", false) {
        it.Println("Печать поколения #: ${current + 1}")
        it.Println()
        for (i in 0 until this.size) {
            it.PrintDiv()
            it.Print("O${i + 1}: ")
            for (j in 0 until this[0].size) {
                it.Print("${this[i][j]} ")
            }
            it.Println()
            it.PrintDiv()

            this[i].makeSchedule(true, NM, it)
            it.Println("_____________________")
            it.Println()
            it.Println()
        }
    }
}

fun ArrayList<Array<Int>>.findBestChild(NM: Array<Array<Int>>): Array<Int> {
    var min = Int.MAX_VALUE
    var pos = 0
    repeat(this.size) {
        val value = this[it].makeSchedule(false, NM, MainPrinter).findMaxInProc()
        if (value < min) {
            min = value
            pos = it
        }
    }

    return this[pos]
}

fun Array<ArrayList<Int>>.findMaxInProc(): Int {
    var tmp = Int.MIN_VALUE

    for (i in 0 until this.size) {
        tmp = if (tmp < this[i].sum()) this[i].sum() else tmp
    }

    return tmp
}

fun Array<Int>.mutation() {
    val randGen = ThreadLocalRandom.current().nextInt(0, size - 1)
    val randBit = ThreadLocalRandom.current().nextInt(0, 7)
    this[randGen] = this[randGen].flipBit(randBit)
}

fun Int.flipBit(position: Int) = this xor (1 shl position)

fun Array<Int>.makeSchedule(print: Boolean, NM: Array<Array<Int>>, printer: Printer): Array<ArrayList<Int>> {
    val tmpArray = Array(NM.size) { ArrayList<Int>() }
    val range = 255 / tmpArray.size

    repeat(this.size) {
        var pos = this[it] / range
        if (pos == tmpArray.size) pos -= 1
        try {
            tmpArray[pos].add(NM[pos][it])
        } catch (e: IndexOutOfBoundsException) {
            throw e
        }
    }

    if (print) {
        repeat(tmpArray.size) { i->
            if (tmpArray[i].size == 0) printer.Print("Empty")
            repeat(tmpArray[i].size) { j ->
                printer.Print("${tmpArray[i][j]} ")
            }
            printer.Println()
        }
        printer.Println()
        printer.Println("Максимальное время: ${tmpArray.findMaxInProc()}")
    }

    return tmpArray
}
