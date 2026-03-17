package cz.cvut.fel.dcgi.zan.practice5.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── Data models ──────────────────────────────────────────────────────────────

enum class Equipment { SLIDE, SWING, SANDBOX, CLIMBING_FRAME, SPRING_RIDER, WATER_PLAY }

@Parcelize
data class Playground(
    val id: Long,
    val name: String,
    val address: String,
    val lat: Double,
    val lon: Double,
    val imageUrl: String,
    val isFavourite: Boolean = false,
    val equipment: List<Equipment> = emptyList(),
) : Parcelable

data class PlannedVisit(
    val id: Long,
    val playground: Playground,
    val dateMillis: Long,
    val hour: Int,
    val minute: Int,
)

// ── Sample data ───────────────────────────────────────────────────────────────

object SampleData {

    val playgrounds = listOf(
        Playground(
            id = 100,
            name = "Na Krejcárku",
            address = "Za Žižkovskou vozovnou 2716/19, Praha 3",
            lat = 50.094387054,
            lon = 14.476410866,
            imageUrl = "http://www.hristepraha.cz/images/img/a0cad32d8f589504e4e67ff2ec2f7e7fo.jpg",
            isFavourite = true,
            equipment = listOf(Equipment.SLIDE, Equipment.SWING, Equipment.SANDBOX),
        ),
        Playground(
            id = 320,
            name = "Kroužky a Proužky",
            address = "Šumavská, Praha 2",
            lat = 50.075458527,
            lon = 14.443644524,
            imageUrl = "http://www.hristepraha.cz/images/img/5b32cbf9a9dc515aadceac62a2c231b3o.jpg",
            isFavourite = true,
            equipment = listOf(Equipment.SWING, Equipment.SPRING_RIDER, Equipment.WATER_PLAY),
        ),
        Playground(
            id = 28,
            name = "Vrch sv. Kříže",
            address = "Sauerova 3, Praha 3",
            lat = 50.084918976,
            lon = 14.460893631,
            imageUrl = "http://www.hristepraha.cz/images/img/19b35a28fe4849c8a5b93f36866865fbo.jpg",
            isFavourite = false,
            equipment = listOf(Equipment.SLIDE, Equipment.CLIMBING_FRAME),
        ),
        Playground(
            id = 221,
            name = "Stará Libeň – U Rokytky",
            address = "Na Rokytce 1029/30, Praha 8",
            lat = 50.110366821,
            lon = 14.474110603,
            imageUrl = "http://www.hristepraha.cz/images/img/e985f2912718e530b12c27c0fc1f9cd9o.jpg",
            isFavourite = false,
            equipment = listOf(Equipment.SANDBOX, Equipment.SPRING_RIDER),
        ),
        Playground(
            id = 85,
            name = "Sídliště Krč",
            address = "Štúrova 537/27, Praha 4",
            lat = 50.024711609,
            lon = 14.450510979,
            imageUrl = "http://www.hristepraha.cz/images/img/4559b569a03bf754c48c1def8d8df4a1o.jpg",
            isFavourite = true,
            equipment = listOf(Equipment.SLIDE, Equipment.SWING, Equipment.CLIMBING_FRAME),
        ),
        Playground(
            id = 72,
            name = "Hostivařský lesopark",
            address = "U Břehu 1111, Praha 15",
            lat = 50.043731689,
            lon = 14.539891243,
            imageUrl = "http://www.hristepraha.cz/images/img/2d73f6832f5ce39fc5987d27d9f4541fo.jpg",
            isFavourite = false,
            equipment = listOf(Equipment.SWING, Equipment.SANDBOX, Equipment.WATER_PLAY),
        ),
    )

    val plannedVisits = listOf(
        PlannedVisit(
            id = 1,
            playground = playgrounds[0],
            dateMillis = System.currentTimeMillis() + 86_400_000L,
            hour = 10,
            minute = 30,
        ),
        PlannedVisit(
            id = 2,
            playground = playgrounds[1],
            dateMillis = System.currentTimeMillis() + 2 * 86_400_000L,
            hour = 14,
            minute = 0,
        ),
    )
}

// ── Helpers ───────────────────────────────────────────────────────────────────

fun Equipment.label(): String = when (this) {
    Equipment.SLIDE          -> "Slide"
    Equipment.SWING          -> "Swing"
    Equipment.SANDBOX        -> "Sandbox"
    Equipment.CLIMBING_FRAME -> "Climbing frame"
    Equipment.SPRING_RIDER   -> "Spring rider"
    Equipment.WATER_PLAY     -> "Water play"
}

fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}

fun formatTime(hour: Int, minute: Int): String =
    "%02d:%02d".format(hour, minute)
