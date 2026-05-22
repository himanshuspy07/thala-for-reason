package com.example.data.engine

import java.util.Stack

object MathEvaluator {

    /**
     * Evaluates a mathematical string expression and returns the result as a Double.
     * Supports +, -, *, /, %, parenthesis, and floating-point decimal numbers.
     */
    fun evaluate(expression: String): Double {
        val cleanExpr = expression.replace(" ", "").replace("x", "*").replace("÷", "/")
        if (cleanExpr.isEmpty()) return 0.0

        return try {
            parseExpr(cleanExpr)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid Math syntax")
        }
    }

    private fun parseExpr(expr: String): Double {
        val tokens = tokenize(expr)
        val values = Stack<Double>()
        val ops = Stack<Char>()

        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]

            if (token[0].isDigit() || (token.length > 1 && token[0] == '-' && token[1].isDigit())) {
                values.push(token.toDouble())
            } else if (token == "(") {
                ops.push('(')
            } else if (token == ")") {
                while (ops.isNotEmpty() && ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                }
                if (ops.isNotEmpty()) ops.pop() // Pop the open parenthesis
            } else if (isOperator(token[0])) {
                while (ops.isNotEmpty() && hasPrecedence(token[0], ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                }
                ops.push(token[0])
            }
            i++
        }

        while (ops.isNotEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()))
        }

        return if (values.isEmpty()) 0.0 else values.pop()
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        val len = expr.length

        while (i < len) {
            val c = expr[i]
            if (c.isWhitespace()) {
                i++
                continue
            }

            // Handle negative numbers or subtraction
            // It's a negative sign if it's '-' and is preceded by start or another operator/parenthesis
            if (c == '-' && (i == 0 || expr[i - 1] == '(' || isOperator(expr[i - 1]))) {
                val sb = StringBuilder()
                sb.append('-')
                i++
                while (i < len && (expr[i].isDigit() || expr[i] == '.')) {
                    sb.append(expr[i])
                    i++
                }
                tokens.add(sb.toString())
                continue
            }

            if (c.isDigit() || c == '.') {
                val sb = StringBuilder()
                while (i < len && (expr[i].isDigit() || expr[i] == '.')) {
                    sb.append(expr[i])
                    i++
                }
                tokens.add(sb.toString())
            } else if (c == '(' || c == ')' || isOperator(c)) {
                tokens.add(c.toString())
                i++
            } else {
                i++ // Skip invalid chars
            }
        }
        return tokens
    }

    private fun isOperator(c: Char): Boolean {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%'
    }

    private fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') return false
        if ((op1 == '*' || op1 == '/' || op1 == '%') && (op2 == '+' || op2 == '-')) return false
        return true
    }

    private fun applyOp(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> {
                if (b == 0.0) throw UnsupportedOperationException("Cannot divide by zero")
                a / b
            }
            '%' -> {
                // If secondary is zero, do standard percentage conversion
                a % b
            }
            else -> 0.0
        }
    }
}
