package main

import java.io.File
import java.util.concurrent.ThreadLocalRandom

class Lab_51 {

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
        Println("Исходная матрица")
        NM.print()

        generate = generateFirstG(Z, M)
        generate.printGenerate(NM, current)

        nextGenerate = ArrayList()
    }

    private fun start() {
        while (repeat != R) {
            while (nextGenerate.size < Z) {
                val bestChild = generate.findBestChild(NM)
                var crossoverCount = 1
                generate.forEach {
                    crossover(crossoverCount++, it, bestChild)
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
    }

    private fun crossover(num: Int, child: Array<Int>, bestChild: Array<Int>) {
        val localGenerate = ArrayList<Array<Int>>()

        if (child === bestChild) return
        if (ThreadLocalRandom.current().nextInt(1, 100) > Pc) return

        val first = Array(M) { 0 }
        val second = Array(M) { 0 }
        val point = ThreadLocalRandom.current().nextInt(1, M - 2)

        Println()
        Println()
        Println(":::::Crossover #$num для поколения #${current + 1}")
        PrintDiv()
        Println("Лучший потомок: ")
        bestChild.forEach {
            Print("$it ")
        }
        Println()
        bestChild.makeSchedule(true, NM, MainPrinter)
        PrintDiv()
        Println("Выбранный потомок: ")
        child.forEach {
            Print("$it ")
        }
        Println()
        child.makeSchedule(true, NM, MainPrinter)
        PrintDiv()

        Println()
        Println(":::::   Точка кроссовера: ${point + 1}")
        Println()

        repeat(M) { i ->
            first[i] = if (i <= point) child[i] else bestChild[i]
            second[i] = if (i <= point) bestChild[i] else child[i]
        }

        fun person(person: Array<Int>, num: Int) {
            PrintDiv()
            Println("Получившийся потомок #$num")
            person.forEach {
                Print("$it ")
            }
            Println()
            person.makeSchedule(true, NM, MainPrinter)
            mutation(person, "Произошла мутация в потомке #$num")
        }

        person(first, 1)
        person(second, 2)
        PrintDiv()

        localGenerate.add(first)
        localGenerate.add(second)
        localGenerate.add(bestChild)

        val newBestChild = localGenerate.findBestChild(NM)

        val newTime = newBestChild.makeSchedule(false, NM, MainPrinter).findMaxInProc()
        val oldTime = bestChild.makeSchedule(false, NM, MainPrinter).findMaxInProc()

        if (newTime <= oldTime) nextGenerate.add(newBestChild)
    }

    private fun mutation(person: Array<Int>, info: String) {
        if (ThreadLocalRandom.current().nextInt(1, 100) > Pm) return

        Println(info)
        person.mutation()
        person.forEach {
            Print("$it ")
        }
        Println()
        person.makeSchedule(true, NM, MainPrinter)
    }
}