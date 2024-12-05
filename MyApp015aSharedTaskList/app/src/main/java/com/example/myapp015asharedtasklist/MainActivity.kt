package com.example.myapp015asharedtasklist

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp015asharedtasklist.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tasks = mutableListOf<Task>() // Lokální seznam úkolů
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavení logiky pro FloatingActionButton
        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
        // Inicializace RecyclerView
        taskAdapter = TaskAdapter(tasks) { task ->
            updateTask(task) // Callback pro změnu úkolu
        }

        // Inicializace Firebase
        FirebaseApp.initializeApp(this)
        println("Firebase initialized successfully")

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTasks.adapter = taskAdapter

        firestore = FirebaseFirestore.getInstance()

        // Simulace načtení dat
        loadTasksFromFirestore()
        listenToTaskUpdates()
    }

    private fun loadTasksFromFirestore() {
        firestore.collection("tasks").get()
            .addOnSuccessListener { result ->
                tasks.clear()
                for (document in result) {
                    val task = document.toObject(Task::class.java)
                    tasks.add(task)
                }
                taskAdapter.notifyDataSetChanged()
                println("Tasks loaded from Firestore")
            }
            .addOnFailureListener { e ->
                println("Error loading tasks: ${e.message}")
            }
    }




    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Task")

        // Vytvoření vstupního pole
        val input = EditText(this)
        input.hint = "Task name"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Tlačítka dialogu
        builder.setPositiveButton("Add") { _, _ ->
            val taskName = input.text.toString()
            if (taskName.isNotBlank()) {
                addTask(taskName)
            } else {
                Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
    private fun addTask(name: String) {
        val newTask = Task(
            id = firestore.collection("tasks").document().id, // Vygenerujeme ID
            name = name,
            isCompleted = false,
            assignedTo = ""
        )

        // Uložíme úkol do Firestore
        firestore.collection("tasks").document(newTask.id).set(newTask)
            .addOnSuccessListener {
                tasks.add(newTask)
                //taskAdapter.notifyItemInserted(tasks.size - 1)
                println("Task added to Firestore: $name")
            }
            .addOnFailureListener { e ->
                println("Error adding task: ${e.message}")
            }
    }


    private fun updateTask(task: Task) {
        // Update task in Firestore
        firestore.collection("tasks").document(task.id!!)
            .set(task) // Replace the entire task document
            .addOnSuccessListener {
                println("Task updated in Firestore: ${task.name}, completed: ${task.isCompleted}")
            }
            .addOnFailureListener { e ->
                println("Error updating task in Firestore: ${e.message}")
            }
    }
    private fun listenToTaskUpdates() {
        firestore.collection("tasks").addSnapshotListener { snapshots, e ->
            if (e != null) {
                println("Listen failed: ${e.message}")
                return@addSnapshotListener
            }

            // Vymažte seznam a aktualizujte jej na základě dat ze serveru
            tasks.clear()
            snapshots?.forEach { document ->
                val task = document.toObject(Task::class.java)
                if (!tasks.any { it.id == task.id }) {
                    tasks.add(task)
                }
            }
            taskAdapter.notifyDataSetChanged()
        }
    }
}