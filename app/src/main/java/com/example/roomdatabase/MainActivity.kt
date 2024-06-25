package com.example.roomdatabase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.GlobalScope

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var studentDatabase: StudentDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        studentDatabase= StudentDatabase.getDatabase(this)

        binding.saveBtn.setOnClickListener {
            saveData()
        }

        binding.showBtn.setOnClickListener {
            startActivity(Intent(this,ShowData::class.java))
        }

        binding.searchBtn.setOnClickListener {
            searchData()
        }
        binding.deleteAllBtn.setOnClickListener {
            GlobalScope.launch {
                studentDatabase.studentDao().deleteAll()
            }
        }

    }

    private fun searchData() {
        val rollNo = binding.searchRollnoEt.text.toString()
        if(rollNo.isNotEmpty()){

            GlobalScope.launch {
                var student:Student = studentDatabase.studentDao().findById(rollNo.toInt())
                if(studentDatabase.studentDao().isEmpty()){
                    Handler(Looper.getMainLooper()).post{
                        Toast.makeText(this@MainActivity, "No data found", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    displayData(student)
                }
            }
        }
    }

    private suspend fun displayData(student: Student) {
        withContext(Dispatchers.Main){
            val fname = student?.firstName.toString()
            val lname = student?.lastName.toString()
            val roll = student?.rollno.toString()

            if (fname.isNullOrEmpty() || lname.isNullOrEmpty() || roll.isNullOrEmpty()) {
                binding.firstnameEt.setText("")
                binding.lastnameEt.setText("")
                binding.rollnoEt.setText("")
                Toast.makeText(this@MainActivity, "No data found", Toast.LENGTH_SHORT).show()
            }else{
                binding.firstnameEt.setText(fname)
                binding.lastnameEt.setText(lname)
                binding.rollnoEt.setText(roll)
            }
        }
    }

    private fun saveData() {
        val firstName = binding.firstnameEt.text.toString()
        val lastName = binding.lastnameEt.text.toString()
        val rollno = binding.rollnoEt.text.toString()

        if (firstName.isNotEmpty() && lastName.isNotEmpty() && rollno.isNotEmpty()) {
            val student = Student(0,firstName,lastName,rollno.toInt())
            GlobalScope.launch {
                studentDatabase.studentDao().insert(student)
            }
            binding.firstnameEt.text?.clear()
            binding.lastnameEt.text?.clear()
            binding.rollnoEt.text?.clear()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }
}