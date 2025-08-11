$(document).ready(function() {
  // Initialize DataTable for roles management
  var table = $('#dataTable').DataTable({
    // Define the layout of the table elements
    dom: "<'row mb-2'<'col-sm-6 text-left'f><'col-sm-6 text-right'B>>" +
          "<'row'<'col-sm-12'tr>>" +
          "<'row'<'col-sm-5'i><'col-sm-7'p>>",
    // Configure the custom buttons for the table header
    buttons: [
      {
        // Export button for Excel
        filename: 'Roles',
        sheetName: 'Roles',
        extend: 'excelHtml5',
        text: '<i class="fas fa-file-excel"></i> Export to Excel',
        className: 'btn btn-success',
        exportOptions: {
            // Export all columns except the last one (the Actions column)
            columns: ':not(:last-child)'
        }
      },
      {
        // Button to open a modal for creating a new role
        text: 'Create New Role <i class="fas fa-plus"></i>',
        action: function ( e, dt, node, config ) {
            $('#newRoleModal').modal('show');
        },
        className: 'btn btn-warning'
      }
    ]
  });
});
