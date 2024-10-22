 package study.nikita.helloworld

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.service.autofill.OnClickAction
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import study.nikita.helloworld.ui.theme.HelloWorldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted: Boolean ->
                if (!granted) {
                    setContent {
                        DefaultList()
                    }
                } else {
                    val result = fetchAllContacts()
                    setContent {
                        ContactsList(result)
                    }
                }

            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                val result = fetchAllContacts()
                setContent {
                    ContactsList(contach = result)
                }
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }
}

data class Contact(val name:String, val phoneNumber: String)

fun Context.fetchAllContacts(): List<Contact> {
    contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        .use { cursor ->
            if (cursor == null) return emptyList()
            val builder = ArrayList<Contact>()
            while (cursor.moveToNext()) {
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val name = if (!nameIndex.equals(-1)) cursor.getString(nameIndex) else "N/A"
                val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val phoneNumber = if (!phoneIndex.equals(-1)) cursor.getString(phoneIndex) else "N/A"

                builder.add(Contact(name, phoneNumber))
            }
            return builder
        }
}

@Composable
fun ContactsList(contach: List<Contact>) {
    LazyColumn (modifier = Modifier.fillMaxSize()) {
        items(contach) { contact ->
            PersonItem(contact = contact)
        }
    }
}

@Composable
fun DefaultList() {
    Column (){
        Text("Контакты не найдены", textAlign = TextAlign.Center)
    }
}


 @Composable
 fun PersonItem(contact: Contact) {
     Column(
         modifier = Modifier.fillMaxSize().padding(16.dp), // Vertically arranged elements
         verticalArrangement = Arrangement.Top, // Space arrangement inside the column
         horizontalAlignment = Alignment.Start // Horizontal alignment of the children
     ) {
         Row (
             modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), // Horizontally arranged elements
             horizontalArrangement = Arrangement.SpaceBetween
         ) {
             Text(text = contact.name);
             Text(text = contact.phoneNumber);
         }
     }
 }