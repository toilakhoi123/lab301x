$(document).ready(function() {
  // Initialize DataTable for blog management
  var table = $('#dataTable').DataTable({
    // Define the layout of the table elements
    dom:  "<'row mb-2'<'col-sm-6 text-left'f><'col-sm-6 text-right'B>>" +
           "<'row'<'col-sm-12'tr>>" +
           "<'row'<'col-sm-5'i><'col-sm-7'p>>",
    // Configure the custom buttons for the table header
    buttons: [
      {
        // Export button for Excel
        filename: 'Blogs',
        sheetName: 'Blogs',
        extend: 'excelHtml5',
        text: '<i class="fas fa-file-excel"></i> Export to Excel',
        className: 'btn btn-success',
        exportOptions: {
          // Specify which columns to export (assuming blog data is in these columns)
          columns: [0, 2, 3, 4]
        }
      },
      {
        // Button to open a modal for creating a new blog post
        text: 'New Post <i class="fas fa-plus"></i>',
        action: function ( e, dt, node, config ) {
          $('#newBlogPostModal').modal('show');
        },
        className: 'btn btn-warning'
      }
    ],
    // Add default sorting by the first column (index 0) in descending order
    order: [[0, 'desc']]
  });
});