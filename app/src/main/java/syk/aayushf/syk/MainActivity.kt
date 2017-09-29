package syk.aayushf.syk

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_scrolling3.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

class MainActivity : AppCompatActivity(), GameConnection.GameInterface , AnkoLogger, GameConnection.GameStartingInterface{
    override fun showResults(listOfResponses: List<String>, ListOfCounts: List<Int>, listOfAuthors: List<String>) {
        allGone()
        mainrv.visibility = VISIBLE
        fab.onClick {
            gc.addRound()
        }
        val listOfResultViews : MutableList<ResultViewItem> = MutableList(listOfAuthors.size, {index ->
            ResultViewItem(ListOfCounts[index].toString(), listOfAuthors[index], listOfResponses[index] )


        })
        val fadap = FastItemAdapter<ResultViewItem>()
        fadap.add(listOfResultViews)
        mainrv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mainrv.adapter = fadap
        fab.visibility = VISIBLE
        fab.onClick {
            gc.addRound()
        }
        setupDrawer()

    }
    var currentResponse:String? = null

    override fun playerAdded(list: List<Player>) {
        val fadap = FastItemAdapter<PlayerViewItem>()
        fadap.add(list.map { PlayerViewItem(it) })
        mainrv.adapter = fadap
        fab.imageResource = R.drawable.ic_arrow_forward_white_48px
        fab.onClick {
            gc.startGame()
        }
        titletv.text = gc.id
        contenttv.text = "Game Code"

    }

    override fun showResponses(lor: List<String>) {
        allGone()
        mainrv.visibility = VISIBLE
        mainrv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val fadap = FastItemAdapter<ResponseViewItem>()
        fadap.add(lor.map { ResponseViewItem(it) })
        fadap.withEventHook(object: ClickEventHook<ResponseViewItem>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                return (viewHolder as ResponseViewItem.ViewHolder).itemVieww
            }
            override fun onClick(v: View?, position: Int, fastAdapter: FastAdapter<ResponseViewItem>?, item: ResponseViewItem?) {

                    gc.respond(item!!.r)


            }

        })
        mainrv.adapter = fadap

    }
    fun allGone(){
        addercv.visibility = GONE
        mainrv.visibility = GONE
        responsecv.visibility = GONE
        questionaddercard.visibility = GONE
    }


    override fun roundAdded(question:String) {
       allGone()
        responsecv.visibility = VISIBLE
        contenttv.text = question
        titletv.visibility = View.INVISIBLE
        fab.visibility = View.INVISIBLE
        submit_response.onClick {
            currentResponse = responseet.text.toString()
            gc.submitResponse(responseet.text.toString())
        }

    }



    

    var gc:GameConnection = GameConnection()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
//
        val fab = findViewById<FloatingActionButton>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            startGame()
        }
        joinfab.onClick {
            joinGame(find<TextInputEditText>(R.id.codeet).text.toString())
        }
        fab_submit_question.onClick {
            gc.addQuestionToDatabase(queset.text.toString())
        }
        setupDrawer()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_add_question) {
            alert{
                customView{
                    title = "Add A Question"
                    verticalLayout {
                        textView {
                            text = "Add #NAME# where the name should come!"

                        }
                        var e:EditText? = null
                        textInputLayout {
                            e = editText {
                                hint = "Enter Question Here"

                            }
                        }
                        positiveButton("Add This Question", {
                            gc.addQuestionToDatabase(e!!.text.toString())
                        })
                    }
                }
            }
            return true
        } else super.onOptionsItemSelected(item)

    }


    fun startGame(){
        alert {
            title = "Player Name"
            customView {
                val e = editText {

                }
                positiveButton("Start",{
                    val p = Player(e.text.toString())
                    gc.createNewGame(p)
                    addGameStarterFrag()

                })
            }
        }.show()
    }
    fun joinGame(code:String){
        alert {
            title = "Player Name"
            customView {
                val e = editText {

                }
                positiveButton("Start",{
                    val p = Player(e.text.toString())
                    gc.joinGame(p, code)
                    addGameStarterFrag()

                })
            }
        }.show()
    }
    fun addGameStarterFrag(){
        allGone()
        mainrv.visibility = VISIBLE
        mainrv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        gc.subscribeToPlayerAddition(this)
        gc.setupGameStartingListener()


    }
    override fun gameStarted(){
        gc.subscribeToGame(this)

    }
    fun setupDrawer(){
        drawer{
            accountHeader {
                profile (getAccount().first, getAccount().second){

                }

            }
            primaryItem("Change Profile"){
                onClick { view, position, drawerItem ->
                    startActivity<ProfileActivity>()
                    false

                }
            }

        }
    }
    fun getAccount():Pair<String, String>{
        val sp = getPreferences(Context.MODE_PRIVATE)
        val name = sp.getString("NAME_PREF", "UnSpecified")
        val id = sp.getString("ID_PREF", UUID.randomUUID().toString().substring(0,7))
        return Pair(name, id)

    }



}
