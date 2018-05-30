package com.vogella.retrofitgithub.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.vogella.retrofitgithub.controllers.GithubController
import com.vogella.retrofitgithub.models.GithubIssue
import com.vogella.retrofitgithub.models.GithubRepo
import com.vogella.retrofitgithub.services.GithubAPI
import com.vogella.retrofitgithub.ui.fragments.CredentialsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import com.vogella.retrofitgithub.R

class MainActivity : AppCompatActivity() , CredentialsDialog.ICredentialsDialogListener {

    private var githubAPI: GithubAPI? = null
    private var username: String = ""
    private var password: String = ""
    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(my_toolbar)
        repositories_spinner.isEnabled = false
        repositories_spinner.adapter = ArrayAdapter(this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item, arrayOf(getString(R.string.no_repositories_available)))

        repositories_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent?.getSelectedItem() is GithubRepo) {
                    val githubRepo = parent.getSelectedItem() as GithubRepo
                    compositeDisposable.add(githubAPI?.getIssues(githubRepo.owner!!, githubRepo.name!!)
                            ?.subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.subscribeWith(getIssuesObserver()))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val githubController = GithubController()
        githubAPI = githubController.createGithubAPI()
    }

    override fun onStop() {
        super.onStop()
        if (compositeDisposable != null && !compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.menu_credentials -> {
                showCredentialsDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDialogPositiveClick(username: String, password: String) {
        this.username = username
        this.password = password
        loadRepos_button.setEnabled(true)
    }

    private fun showCredentialsDialog() {
        val dialog = CredentialsDialog()
        val arguments = Bundle()
        arguments.putString("username", username)
        arguments.putString("password", password)
        dialog.arguments = arguments

        dialog.show(supportFragmentManager, "credentialsDialog")
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.loadRepos_button -> compositeDisposable.add(githubAPI?.getRepos()!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(getRepositoriesObserver()))
        }
    }

    private fun getIssuesObserver(): DisposableSingleObserver<List<GithubIssue>> {
        return object : DisposableSingleObserver<List<GithubIssue>>() {
            override fun onSuccess(value: List<GithubIssue>) {
                if (!value.isEmpty()) {
                    val spinnerAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, value)
                    issues_spinner.setEnabled(true)
                    issues_spinner.setAdapter(spinnerAdapter)
                } else {
                    val spinnerAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, arrayOf("Repository has no issues"))
                    issues_spinner.setEnabled(false)
                    issues_spinner.setAdapter(spinnerAdapter)
                }
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Can not load issues", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getRepositoriesObserver(): DisposableSingleObserver<List<GithubRepo>> {
        return object : DisposableSingleObserver<List<GithubRepo>>() {
            override fun onSuccess(value: List<GithubRepo>) {
                if (!value.isEmpty()) {
                    val spinnerAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, value)
                    repositories_spinner.setAdapter(spinnerAdapter)
                    repositories_spinner.setEnabled(true)
                } else {
                    val spinnerAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, arrayOf("User has no repositories"))
                    repositories_spinner.setAdapter(spinnerAdapter)
                    repositories_spinner.setEnabled(false)
                }
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Can not load repositories", Toast.LENGTH_SHORT).show()

            }
        }
    }
}
