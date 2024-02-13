import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

// >0 - right
// <0 - left
// Определение поворота (расположение С относительно АВ)
fun rotate(A : Offset, B: Offset, C: Offset): Float {
    return (B.x-A.x)*(C.y-B.y)-(B.y-A.y)*(C.x-B.x)
}

fun grahamscan(Dataset_point : MutableList<Offset>): MutableList<Int> {

    var Data_size = Dataset_point.size
    val Numeral_array = IntArray(Data_size) { it } // массив с косвенной адресацией на Dataset
                                                   // работа будет происходить относительно Numeral_array (отрисовка)

    // Заполнение Numeral_array где первая точка - начало МВО
    for (i in 1..Data_size-1) {

        if (Dataset_point[Numeral_array[i]].x < Dataset_point[Numeral_array[0]].x){ // если Numeral_array[i]-ая точка
                                                                                    // лежит левее Numeral_array[0]-ой
            var temp = Numeral_array[i]
            Numeral_array[i] = Numeral_array[0]
            Numeral_array[0] = temp
        }

    }

    //println(P.joinToString())

    // Сортировка по степени "левизны" (rotate) без начальной точки в Numeral_array
    for (i in 2..Data_size-1) {

        var j = i
        while (j > 1 && rotate(Dataset_point[Numeral_array[0]], Dataset_point[Numeral_array[j - 1]],
                Dataset_point[Numeral_array[j]]) < 0f){
            var temp = Numeral_array[j]
            Numeral_array[j] = Numeral_array[j-1]
            Numeral_array[j-1] = temp
            j -= 1
        }

    }

    var Numeral_array_exit = mutableListOf<Int>()

    // Срезаем ненужные углы для обретения формы МВО
    // Убираем все левые точки относительно последних двух вершин в стеке Numeral_array_exit
    // Необходима проверка на size>2 поскольку функция начнет вызов сразу, а мы точки можем не успеть проставить > 2
    if (Numeral_array.size > 2){
        Numeral_array_exit.add(Numeral_array[0]); Numeral_array_exit.add(Numeral_array[1])

        // Сам срез углов
        // Нам необходимо, чтобы все точки были левыми из-за нашего обхода (против часовой стрелки)
        // Если вдруг какая-то точка стало правой, то мы удаляем из обхода точку, являющейся концом вектора,
        // относительно которого образовалась правая точка
        for (i in (2..Data_size - 1)) {
            while (rotate(Dataset_point[Numeral_array_exit.getOrNull(Numeral_array_exit.size - 2) ?: error("Index out of bounds")], Dataset_point[Numeral_array_exit.getOrNull(Numeral_array_exit.size - 1) ?: error("Index out of bounds")], Dataset_point[Numeral_array[i]]) < 0f) {
                Numeral_array_exit.removeAt(Numeral_array_exit.size - 1)
            }
            Numeral_array_exit.add(Numeral_array[i])
        }
        //println(Numeral_array_exit.joinToString())
    }

    return Numeral_array_exit
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun Shape()
{/*Shape start*/

    var Dataset_points = mutableListOf<Offset>()

    Canvas(modifier = Modifier.fillMaxSize().clickable{}.onPointerEvent(PointerEventType.Press){/* oPE open*/
        Dataset_points.add(it.changes.first().position)
    /*OPE close*/})

    {/* Canvas open
     MAIN DRAWING*/

        // Drawing points on the screen (touching)
        for(point in Dataset_points) {
            drawCircle(color = Color.Black, radius = 5f, center = point)
        }

        var Numeral = grahamscan(Dataset_points)

        if (Numeral.size > 2) {
            for (i in 0..Numeral.size - 2) {
                drawLine(color = Color.Black, Dataset_points[Numeral[i]], Dataset_points[Numeral[i + 1]])
            }

            drawLine(color = Color.Black, Dataset_points[Numeral[0]], Dataset_points[Numeral[Numeral.size-1]])
        }

    /* Canvas close*/}
/* Shape close*/}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        Shape()
    }
}
