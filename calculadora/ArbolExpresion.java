import java.util.Stack;
import java.util.Scanner;

/**
 * Clase que permite la construcción y evaluación de expresiones matemáticas en notación postfija.
 */
class Nodo {
    String valor;  //Tipo a String para manejar números con varios dígitos
    Nodo izquierdo;
    Nodo derecho;

    public Nodo(String valor) {
        this.valor = valor;
        izquierdo = null;
        derecho = null;
    }

    // Constructor adicional para operadores
    public Nodo(char operador) {
        this.valor = Character.toString(operador);
        izquierdo = null;
        derecho = null;
    }

    // Método para verificar si un nodo es un número
    public boolean esNumero() {
        try {
            Double.parseDouble(valor);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

/**
 * Clase principal para manipular expresiones matemáticas.
 */
public class ArbolExpresion {

    /**
     * Construye un árbol de expresión a partir de una expresión postfija.
     *
     * @param expresionPostfija La expresión en notación postfija.
     * @return La raíz del árbol de expresión.
     */
    public static Nodo construirArbol(String expresionPostfija) {
        Stack<Nodo> pila = new Stack<>();
        String[] tokens = expresionPostfija.split("\\s+");

        for (String token : tokens) {
            if (token.matches("[0-9]+(\\.[0-9]+)?")) {
                // Si es un número, crea un nodo y lo apila en la pila
                Nodo nodo = new Nodo(token);
                pila.push(nodo);
            } else if (esOperador(token.charAt(0))) {
                // Si es un operador, crea un nodo y le asigna los dos nodos superiores de la pila como hijos
                Nodo nodo = new Nodo(token.charAt(0));
                nodo.derecho = pila.pop();
                nodo.izquierdo = pila.pop();
                pila.push(nodo);
            }
        }
        // El último nodo en la pila es la raíz del árbol
        return pila.pop();
    }

    /**
     * Verifica si un carácter es un operador.
     *
     * @param c El carácter a verificar.
     * @return true si es un operador, false en caso contrario.
     */
    public static boolean esOperador(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' ||
               c == '!' || c == '^' || c == '&' || c == '|' || c == '~';
    }

    /**
     * Evalúa un árbol de expresión.
     *
     * @param raiz La raíz del árbol de expresión.
     * @return El resultado de la evaluación de la expresión.
     */
    public static double evaluarArbol(Nodo raiz) {
        if (raiz == null) {
            return 0;
        }
    
        if (raiz.esNumero()) {
            // Si el nodo actual es un número, devuelve su valor como double.
            return Double.parseDouble(raiz.valor);
        }
    
        double izquierdo = evaluarArbol(raiz.izquierdo);
        double derecho = evaluarArbol(raiz.derecho);
    
        // Realiza la operación correspondiente según el operador del nodo
        switch (raiz.valor.charAt(0)) {
            case '+':
                return izquierdo + derecho;
            case '-':
                return izquierdo - derecho;
            case '*':
                return izquierdo * derecho;
            case '/':
                return izquierdo / derecho;
            case '%':
                return izquierdo % derecho;
            case '!':
                return Math.pow(izquierdo, derecho);
            case '&':
                return (izquierdo != 0 && derecho != 0) ? 1 : 0;
            case '|':
                return (izquierdo != 0 || derecho != 0) ? 1 : 0;
            case '~':
                return (izquierdo == 0) ? 1 : 0;
            case '^':
                return (izquierdo != derecho) ? 1 : 0;
        }
    
        return 0;
    }

/**
* Convierte una expresión matemática en notación infija a notación postfija.
*
* @param expresionInfija La expresión en notación infija.
* @return La expresión en notación postfija.
*/
public static String infijaAPostfija(String expresionInfija) {
    String expresionPostfija = "";  // Aquí se inicializa la cadena donde se almacenará la expresión en notación postfija.
    Stack<Character> pila = new Stack<>();  // Se crea una pila para ayudar en la conversión.

    for (int i = 0; i < expresionInfija.length(); i++) {
        char c = expresionInfija.charAt(i);  // Se recorre la expresión infija caracter por caracter.

        if (Character.isDigit(c) || c == '.') {
            // Si el carácter es un dígito o un punto decimal, se está construyendo un número.
            // Se entra en un bucle para obtener el número completo, permitiendo números con más de un dígito y decimales.
            String numero = "";
            while (i < expresionInfija.length() && (Character.isDigit(expresionInfija.charAt(i)) || expresionInfija.charAt(i) == '.')) {
                numero += expresionInfija.charAt(i);  // Se construye el número.
                i++;
            }
            expresionPostfija += numero;  // Se agrega el número a la expresión postfija.
            expresionPostfija += ' ';  // Se agrega un espacio para separar los números/operandos.
            i--;  // Se retrocede una posición en la cadena para evitar que se omita un carácter.
        } else if (c == '(') {
            pila.push(c);  // Si es un paréntesis de apertura, se agrega a la pila.
        } else if (c == ')') {
            // Si es un paréntesis de cierre, se desapilan operadores y se agregan a la expresión postfija hasta encontrar el paréntesis de apertura correspondiente.
            while (!pila.isEmpty() && pila.peek() != '(') {
                expresionPostfija += pila.pop();  // Se desapila y agrega a la expresión postfija.
                expresionPostfija += ' ';  // Se agrega un espacio.
            }
            pila.pop();  // Se elimina el paréntesis de apertura de la pila.
        } else if (esOperador(c)) {
            // Si es un operador, se compara su prioridad con los operadores en la pila y se decide si desapilar y agregar operadores a la expresión postfija.
            while (!pila.isEmpty() && prioridad(c) <= prioridad(pila.peek())) {
                expresionPostfija += pila.pop();  // Se desapila y agrega a la expresión postfija.
                expresionPostfija += ' ';  // Se agrega un espacio.
            }
            pila.push(c);  // Finalmente, se agrega el operador a la pila.
        }
    }

    // Después de procesar toda la expresión, se desapilan los operadores restantes en la pila y se agregan a la expresión postfija.
    while (!pila.isEmpty()) {
        expresionPostfija += pila.pop();  // Se desapila y agrega a la expresión postfija.
        expresionPostfija += ' ';  // Se agrega un espacio.
    }

    return expresionPostfija.trim();  // Se retorna la expresión postfija sin espacios adicionales al principio o al final.
}


    /**
     * Determina la prioridad de los operadores.
     *
     * @param operador El operador a evaluar.
     * @return El valor de prioridad del operador.
     */
    public static int prioridad(char operador) {
        if (operador == '+' || operador == '-') {
            return 1;
        } else if (operador == '*' || operador == '/') {
            return 2;
        } else if (operador == '%' || operador == '!' || operador == '^') {
            return 3;
        } else if (operador == '&' || operador == '|') {
            return 4;
        } else if (operador == '~') {
            return 5;
        } else if (operador == '(') {
            return 0;
        } else {
            return 0;
        }
    }

    /**
     * Método principal que permite al usuario ingresar una expresión en notación infija,
     * la convierte a notación postfija, construye el árbol de expresión y evalúa el resultado.
     *
     * @param args Los argumentos de la línea de comandos (no se utilizan en este programa).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingresa una expresión en notación infija: ");
        String expresionInfija = scanner.nextLine();
        scanner.close();

        String expresionPostfija = infijaAPostfija(expresionInfija);
        System.out.println("Expresión en notación postfija: " + expresionPostfija);

        Nodo raiz = construirArbol(expresionPostfija);
        double resultado = evaluarArbol(raiz);
        System.out.println("Resultado de la expresión: " + resultado);
    }
}
