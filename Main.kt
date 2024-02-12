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

    var n = Dataset_point.size
    val P = IntArray(n) { it }

    //println(P.joinToString())

    for (i in 1..n-1) {
        if (Dataset_point[P[i]].x < Dataset_point[P[0]].x){ // если P[i]-ая точка лежит левее P[0]-ой точки
            var temp = P[i]
            P[i] = P[0]
            P[0] = temp
        }
    }

    //println(P.joinToString())

    for (i in 2..n-1) {
        var j = i
        while (j > 1 && rotate(Dataset_point[P[0]], Dataset_point[P[j - 1]], Dataset_point[P[j]]) < 0f){
            var temp = P[j]
            P[j] = P[j-1]
            P[j-1] = temp
            j -= 1
        }
    }

    var S = mutableListOf<Int>()

    if (P.size > 2){
        S.add(P[0]); S.add(P[1])

        for (i in (2..n - 1)) {
            while (rotate(Dataset_point[S.getOrNull(S.size - 2) ?: error("Index out of bounds")], Dataset_point[S.getOrNull(S.size - 1) ?: error("Index out of bounds")], Dataset_point[P[i]]) < 0f) {
                S.removeAt(S.size - 1)
            }
            S.add(P[i])
        }
        println(S.joinToString())
    }
    
    return S
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
