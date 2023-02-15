package com.example.finance.accountUtilities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Button
import com.example.finance.MyFunction.getSerializable
import com.example.finance.MyConstants
import com.example.finance.R
import com.example.finance.database.MyDbManager
import com.example.finance.items.MyAccount

class AccountEditActivity : AppCompatActivity() {

    private lateinit var myDbManager: MyDbManager

    private lateinit var state : String // Состояние. Для добавления, или изменения и удаления
    private lateinit var receivedAccount : MyAccount // Полученный счёт из другой активити

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_account_edit)

        // Активировать ActionBar - меню
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myDbManager = MyDbManager(this)

        val editTextAccountName = findViewById<EditText>(R.id.editTextAccountName)
        val buttonOkAccount = findViewById<Button>(R.id.buttonOkAccount)
        val buttonCancelAccount = findViewById<Button>(R.id.buttonCancelAccount)

        // Получить состояние
        state = intent.getStringExtra(MyConstants.STATE).toString()
        // В зависимости от того какое состояние, выполнить соответствующее действие
        if(state == MyConstants.STATE_ADD){
            receivedAccount = MyAccount()
            buttonOkAccount.text = resources.getText(R.string.add)
            buttonCancelAccount.text = resources.getText(R.string.cancel)
        }
        else if(state == MyConstants.STATE_CHANGE_OR_DELETE){
            receivedAccount = getSerializable(this, MyConstants.KEY_MY_ACCOUNT, MyAccount::class.java)
            editTextAccountName.setText(receivedAccount.name)
            buttonOkAccount.text = resources.getText(R.string.change)
            buttonCancelAccount.text = resources.getText(R.string.remove)
        }

        //Нажатие на кнопку ОК, в зависимости от состояние - state, либо добавляет, либо изменять данные
        buttonOkAccount.setOnClickListener {
            if (state == MyConstants.STATE_ADD){
                val account = MyAccount(0, editTextAccountName.text.toString())
                myDbManager.insertToAccounts(account)
            }
            else if (state == MyConstants.STATE_CHANGE_OR_DELETE){
                val account = MyAccount(receivedAccount._id, editTextAccountName.text.toString())
                myDbManager.updateInAccounts(account)
            }
            finish()
        }

        // Нажатие на кнопку отмена, в зависимости от состояние - state, либо закрывает страницу,
        // либо удаляет данные
        buttonCancelAccount.setOnClickListener {
            if (state == MyConstants.STATE_CHANGE_OR_DELETE){
                myDbManager.deleteFromAccounts(receivedAccount._id)
            }
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDatabase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
    }
}
