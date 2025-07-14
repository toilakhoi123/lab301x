$(document).ready(function() {
  var table = $('#dataTable').DataTable({
    dom:  "<'row mb-2'<'col-sm-6 text-left'f><'col-sm-6 text-right'B>>" +
          "<'row'<'col-sm-12'tr>>" +
          "<'row'<'col-sm-5'i><'col-sm-7'p>>",
    buttons: [
      {
        filename: 'Donations',
        sheetName: 'Donations',
        extend: 'excelHtml5',
        text: '<i class="fas fa-file-excel"></i> Export to Excel',
        className: 'btn btn-success',
        exportOptions: {
          columns: [0, 1, 2, 3, 4],
          format: {
            body: function ( data, _, column, _ ) {
              // process data for account status column
              if (column === 4) {
                if (data.includes('✔️')) {
                  return 'true';
                } else if (data.includes('❌')) {
                  return 'false';
                }
              }
              return data.replaceAll("<span>", "").replaceAll("</span>", "");
            }
          }
        }
      }
    ]
  });
});