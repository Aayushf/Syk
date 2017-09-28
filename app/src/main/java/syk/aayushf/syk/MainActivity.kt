package syk.aayushf.syk

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.design.themedAppBarLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_scrolling3.*
import android.view.View.VISIBLE
import android.view.View.GONE
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook

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



}
