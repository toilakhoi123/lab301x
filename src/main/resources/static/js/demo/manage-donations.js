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
          columns: [0, 1, 2, 3, 4, 5],
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

  // paramed search
  const params = new URLSearchParams(window.location.search);
  const searchCampaign = params.get('campaign');
  const searchAccount = params.get('account');

  if (searchCampaign) {
      table.column(1).search(`[id:${searchCampaign}]`).draw();
  }
  if (searchAccount) {
      table.column(2).search(`[id:${searchAccount}]`).draw();
  }
});