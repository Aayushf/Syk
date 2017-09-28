package syk.aayushf.syk

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*

/**
 * Created by aayushf on 13/9/17.
 */
class GameConnection(var id: String = UUID.randomUUID().toString().substring(0, 7)) : AnkoLogger {
    var host: Boolean = false
    var myplayer = Player()
    var currentround = 0
    var listofques = listOf<String>()
    var listOfPlayers = listOf<String>()

    interface GameStartingInterface {
        fun playerAdded(list: List<Player>)
        fun gameStarted()
    }

    interface GameInterface {
        fun roundAdded(question: String)
        fun showResponses(lor: List<String>)
        fun showResults(listOfResponses: List<String>, ListOfCounts: List<Int>, listOfAuthors: List<String>)

    }

    var gi: GameInterface? = null

    var gamestarterlistener: GameStartingInterface? = null
    fun createNewGame(initPlayer: Player) {
        val fbd = FirebaseDatabase.getInstance()
        val gamesRef = fbd.getReference("games")
        val gameRef = gamesRef.child(id)
        gameRef.child("id").setValue(id)
        gameRef.child("gamestate").setValue("STARTING")
        gameRef.child("currentround").setValue(0)
        val gameplayersref = gameRef.child("players")
        gameplayersref.child(initPlayer.id).setValue(initPlayer)
        host = true
        myplayer = initPlayer
        downloadQuestions()
    }

    fun joinGame(player: Player, gamecode: String) {
        myplayer = player
        val fbd = FirebaseDatabase.getInstance()

        val gamesRef = fbd.getReference("games")
        val gameRef = gamesRef.child(gamecode)
        gameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                this@GameConnection.id = p0.child("id").getValue(String::class.java)
            }

        })
        gameRef.child("players").child(player.id).setValue(player)
        info(gamecode)
        this.id = gamecode
        info(id)

    }

    fun subscribeToPlayerAddition(act: MainActivity) {
        gamestarterlistener = act
        val fbd = FirebaseDatabase.getInstance()
        val gameref = fbd.getReference("games").child(id).child("players")
        gameref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val lop: List<Player> = p0.children.map { it.getValue(Player::class.java) }
                listOfPlayers = lop.map { it.name }
                gamestarterlistener!!.playerAdded(lop)
            }

        })
        gameref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val lop: List<Player> = p0.children.map { it.getValue(Player::class.java) }
                gamestarterlistener!!.playerAdded(lop)
            }

        })


    }

    fun showresultsact() {
        val fbd = FirebaseDatabase.getInstance()
        fbd.getReference("games").child(id).child("rounds").child("curround").child("responses").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val listOfResponses: List<String> = p0.children.map { it.child("response").getValue(String::class.java) }
                val listOfSelections: List<Int> = p0.children.map { it.child("chosenby").childrenCount.toInt() }
                val listOfAuthors: List<String> = p0.children.map { it.child("author").getValue(String::class.java) }
                gi!!.showResults(listOfResponses, listOfSelections, listOfAuthors)

            }

        })
    }

    fun setupGameStartingListener() {
        val fbd = FirebaseDatabase.getInstance()
        val gamesRef = fbd.getReference("games")
        val gameRef = gamesRef.child(id)
        gameRef.child("gamestate").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val gamestate = p0.value as String
                if (gamestate == "STARTED") {
                    gamestarterlistener!!.gameStarted()
                }

            }

        })

    }

    fun downloadQuestions() {
        val fbd = FirebaseDatabase.getInstance()
        val questionsref = fbd.getReference("questions")
        questionsref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                listofques = p0.children.map { it.value as String }
            }

        })

    }

    fun subscribeToGame(act: MainActivity) {
        gi = act
        val fbd = FirebaseDatabase.getInstance()
        val gamestate = fbd.getReference("games").child(id).child("gamestate").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value == "ROUNDADDED") {
                    triggerRoundAdded()

                } else if (p0.value == "RESPONDED") {
                    triggerResponsesAction()
                } else if (p0.value == "ALLRESPONDED") {
                    showresultsact()
                }
            }

        })
    }

    fun triggerRoundAdded() {
        val fbd = FirebaseDatabase.getInstance()
        fbd.getReference("games").child(id).child("rounds").child("curround").child("question").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value != null) {
                    gi!!.roundAdded(p0.value as String)
                }
            }

        })
    }

    fun addRound() {
        if (listofques.isNotEmpty()) {
            if (listOfPlayers.isNotEmpty()) {
                val fbd = FirebaseDatabase.getInstance()
                fbd.getReference("games").child(id).child("rounds").child("curround").child("question").setValue(generateQuestion())
                fbd.getReference("games").child(id).child("gamestate").setValue("ROUNDADDED")
                fbd.getReference("games").child(id).child("rounds").child("curround").child("responses").removeValue()
                fbd.getReference("games").child(id).child("rounds").child("curround").child("responded").removeValue()


            }
        }
    }

    fun startGame() {
        if (host) {
            addRound()
            val fbd = FirebaseDatabase.getInstance()
            fbd.getReference("games").child(id).child("gamestate").setValue("STARTED")
            subscribeToResponses()
        } else {

        }
    }

    fun generateQuestion(): String {
        val rand = Random()
        val radomquestion = listofques[rand.nextInt(listofques.size)]
        val radomplayer = listOfPlayers[rand.nextInt(listOfPlayers.size)]
        val formattedques = radomquestion.replace("#NAME#", radomplayer, false)
        return formattedques
    }

    fun submitResponse(response: String) {
        val fbd = FirebaseDatabase.getInstance()
        fbd.getReference("games").child(id).child("rounds").child("curround").child("responses").child(response).child("response").setValue(response)
        fbd.getReference("games").child(id).child("rounds").child("curround").child("responses").child(response).child("author").setValue(myplayer.name)

    }

    fun subscribeToResponses() {
        val fbd = FirebaseDatabase.getInstance()

        fbd.getReference("games").child(id).child("rounds").child("curround").child("responses").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.children.count() == listOfPlayers.count()) {
                    info("All Players Responded")
                    FirebaseDatabase.getInstance().getReference("games").child(id).child("gamestate").setValue("RESPONDED")
                    triggerResponsesAction()
                    subscribeToSelections()
                } else {
                    info("response Logged")
                }
            }

        })


    }

    fun subscribeToSelections() {
        val fbd = FirebaseDatabase.getInstance()
        fbd.getReference("games").child(id).child("rounds").child("curround").child("responded").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.childrenCount.toInt() == listOfPlayers.count()) {
                    triggerAllRespondedAction()

                }

            }

        })
    }

    fun triggerAllRespondedAction() {
        val fbd = FirebaseDatabase.getInstance()
        fbd.getReference("games").child(id).child("gamestate").setValue("ALLRESPONDED")
    }

    fun triggerResponsesAction() {
        val fbd = FirebaseDatabase.getInstance()
        fbd.getReference("games").child(id).child("rounds").child("curround").child("responses").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val ListOfResponses = p0.children.map { it.child("response").getValue(String::class.java) }
                gi!!.showResponses(ListOfResponses)
            }

        })


    }

    fun addQuestionToDatabase(question: String) {
        val fbd = FirebaseDatabase.getInstance()
        val questionsref = fbd.getReference("questions").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                fbd.getReference("questioncount").setValue(p0.childrenCount)
                fbd.getReference("questions").child(p0.childrenCount.toString()).setValue(question)


            }

        })
    }

    fun respond(response: String) {
        FirebaseDatabase.getInstance().getReference("games").child(id).child("rounds").child("curround").child("responses").child(response).child("chosenby").child(myplayer.name).setValue(true)
        FirebaseDatabase.getInstance().getReference("games").child(id).child("rounds").child("curround").child("responded").child(myplayer.name).setValue(true)

    }


}