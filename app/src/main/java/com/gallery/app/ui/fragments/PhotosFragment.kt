// ui/fragments/PhotosFragment.kt
package com.gallery.app.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gallery.app.R
import com.gallery.app.databinding.FragmentPhotosBinding
import com.gallery.app.ui.adapters.MediaAdapter
import com.gallery.app.ui.viewmodels.PhotosViewModel
import com.google.android.material.snackbar.Snackbar

class PhotosFragment : Fragment() {
    
    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PhotosViewModel by viewModels()
    private lateinit var adapter: MediaAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
        
        viewModel.loadPhotos()
    }
    
    private fun setupRecyclerView() {
        adapter = MediaAdapter(requireContext(), listOf()) { mediaItem ->
            // Navigate to full-screen viewer
            val intent = Intent(requireContext(), FullScreenViewerActivity::class.java).apply {
                putExtra("media_item", Gson().toJson(mediaItem))
                putExtra("media_list", Gson().toJson(viewModel.currentList.value))
                putExtra("position", adapter.currentPosition)
            }
            startActivity(intent)
        }
        
        binding.recyclerViewPhotos.apply {
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@PhotosFragment.adapter
            addItemDecoration(GridSpacingItemDecoration(3, 8, true))
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshPhotos()
        }
    }
    
    private fun observeViewModel() {
        viewModel.photos.observe(viewLifecycleOwner) { photos ->
            adapter.updateItems(photos)
            binding.swipeRefresh.isRefreshing = false
            
            if (photos.isEmpty()) {
                binding.textEmpty.visibility = View.VISIBLE
                binding.recyclerViewPhotos.visibility = View.GONE
            } else {
                binding.textEmpty.visibility = View.GONE
                binding.recyclerViewPhotos.visibility = View.VISIBLE
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.errorShown()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}