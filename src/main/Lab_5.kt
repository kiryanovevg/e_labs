package main

import java.io.File
import java.util.concurrent.ThreadLocalRandom

class Lab_5 {

    var N   = 0
    var M   = 0
    var t1  = 0
    var t2  = 0
    var Z   = 0     // Количество особей в поколении
    var Pc  = 0     // вероятность мутации [0..100]
    var Pm  = 0     // аероятность мутации [0..100]
    var R   = 0     // количество повторов

    var repeat  = 0
    var current = 0 // текущее поколение

    lateinit var NM: Array<Array<Int>>

    lateinit var generate: ArrayList<Array<Int>>
    lateinit var nextGenerate: ArrayList<Array<Int>>

    init {
        initializeValues()
        start()
    }

    private fun initializeValues() {
        File(outputFileName).deleteRecursively()
        File(generationPath).deleteRecursively()
        File(transformationPath).deleteRecursively()

        N = tryGet("N - процессор")
        M = tryGet("M - кол, задач")

        var t1Bt2 = false
        while (!t1Bt2) {
            t1 = tryGet("t1")
            t2 = tryGet("t2")
            if (t1 <= t2) t1Bt2 = true
            else Print(TAGerror, "t2 должно быть >=, чем t2")
        }

        Z = tryGet("Z - Колличество особей в поколении")
        Pc = tryGet("Pc - Вероятность скрещивания [0..100]")
        Pm = tryGet("Pm - Вероятность Мутации [0..100]")
        R = tryGet("R - Колличество повторов")

        NM = makeHomogeneous(N, M, t1, t2)
        /*val row = arrayOf(19, 16, 18, 13, 14, 19, 19, 14)
        NM = Array(N) {
            row.clone()
        }*/

        Println("Исходная матрица")
        NM.print()

        generate = generateFirstG(Z, M)
        generate.printGenerate(NM, current)

        nextGenerate = ArrayList()
    }

    private fun start() {
        while (repeat != R) {
            var crossoverCount = 1
            while (nextGenerate.size < Z) {
                for (i: Int in 0 until generate.size) {
                    val item = generate[i]
                    val l = ThreadLocalRandom.current().nextInt(0, generate.size - 1)
//                    if (crossover(crossoverCount, it, generate[l], MainPrinter)) crossoverCount += 1
                    printInFile(transformationPath, "Transformation#${current + 1}", true) { printer ->
                        if (crossover(crossoverCount, item, generate[l], printer)) {
                            Println(":::::Crossover #$crossoverCount для поколения #${current + 1}")
                            crossoverCount += 1
                        }
                    }
                    if (nextGenerate.size == Z) break
                }
            }

            current += 1
            nextGenerate.printGenerate(NM, current)

            // Проверка на повтор
            val oldTime = generate.findBestChild(NM).makeSchedule(false, NM, MainPrinter).findMaxInProc()
            val newTime = nextGenerate.findBestChild(NM).makeSchedule(false, NM, MainPrinter).findMaxInProc()

            if (oldTime == newTime) repeat++

            generate.clear()
            generate.addAll(nextGenerate)

            nextGenerate = ArrayList()
        }

        PrintDiv()
        PrintDiv()
        PrintDiv()
        Println("Результат:")
        generate.findBestChild(NM).forEach {
            Print("$it ")
        }
        Println()
        generate.findBestChild(NM).makeSchedule(true, NM, MainPrinter)
    }

    private fun crossover(num: Int, child: Array<Int>, bestChild: Array<Int>, printer: Printer): Boolean {
        val localGenerate = ArrayList<Array<Int>>()

        if (child === bestChild) return false
        if (ThreadLocalRandom.current().nextInt(1, 100) > Pc) return false

        val first = Array(M) { 0 }
        val second = Array(M) { 0 }
        val point = ThreadLocalRandom.current().nextInt(1, M - 2)

        printer.Println()
        printer.Println()
        printer.Println(":::::Crossover #$num для поколения #${current + 1}")
        printer.PrintDiv()
        printer.Println("Лучший потомок: ")
        bestChild.forEach {
            printer.Print("$it ")
        }
        printer.Println()
        bestChild.makeSchedule(true, NM, printer)
        printer.PrintDiv()
        printer.Println("Выбранный потомок: ")
        child.forEach {
            printer.Print("$it ")
        }
        printer.Println()
        child.makeSchedule(true, NM, printer)
        printer.PrintDiv()

        printer.Println()
        printer.Println(":::::   Точка кроссовера: ${point + 1}")
        printer.Println()

        repeat(M) { i ->
            first[i] = if (i <= point) child[i] else bestChild[i]
            second[i] = if (i <= point) bestChild[i] else child[i]
        }

        fun person(person: Array<Int>, num: Int) {
            printer.PrintDiv()
            printer.Println("Получившийся потомок #$num")
            person.forEach {
                printer.Print("$it ")
            }
            printer.Println()
            person.makeSchedule(true, NM, printer)
            mutation(person, "Произошла мутация в потомке #$num", printer)
        }

        person(first, 1)
        person(second, 2)
        printer.PrintDiv()

        localGenerate.add(first)
        localGenerate.add(second)
        localGenerate.add(bestChild)

        val newBestChild = localGenerate.findBestChild(NM)

//        val newTime = newBestChild.makeSchedule(false, NM, MainPrinter).findMaxInProc()
//        val oldTime = bestChild.makeSchedule(false, NM, MainPrinter).findMaxInProc()
//        if (newTime <= oldTime) nextGenerate.add(newBestChild)
        nextGenerate.add(newBestChild)

        return true
    }

    private fun mutation(person: Array<Int>, info: String, printer: Printer) {
        if (ThreadLocalRandom.current().nextInt(1, 100) > Pm) return

        printer.Println(info)
        person.mutation()
        person.forEach {
            printer.Print("$it ")
        }
        printer.Println()
        person.makeSchedule(true, NM, printer)
    }
}