package nz.ac.uclive.itw21.project2.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import nz.ac.uclive.itw21.project2.R
import nz.ac.uclive.itw21.project2.database.DeviceViewModel

class DeviceFragment : Fragment() {
    private lateinit var adapter: DeviceAdapter
    private lateinit var deviceViewModel: DeviceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = activity?.let { DeviceAdapter(it) }!!
        deviceViewModel = ViewModelProvider(this).get(DeviceViewModel::class.java)

        deviceViewModel.devices.observe(this, Observer {devices ->
            devices?.let{ adapter.setDeviceList(it) }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_device_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_device_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        return view
    }
}
