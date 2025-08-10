$(document).ready(function() {
  var table = $('#dataTable').DataTable({
    dom:  "<'row mb-2'<'col-sm-6 text-left'f><'col-sm-6 text-right'B>>" +
          "<'row'<'col-sm-12'tr>>" +
          "<'row'<'col-sm-5'i><'col-sm-7'p>>",
    buttons: [
      {
        filename: 'Campaigns',
        sheetName: 'Campaigns',
        extend: 'excelHtml5',
        text: '<i class="fas fa-file-excel"></i> Export to Excel',
        className: 'btn btn-success',
        exportOptions: {
          columns: [0, 1, 2, 3, 4, 5, 6, 7]
        }
      },
      {
        text: 'New Campaign <i class="fas fa-plus"></i>',
        // text: 'New Campaign',
        action: function ( e, dt, node, config ) {
          $('#newCampaignModal').modal('show');
        },
        className: 'btn btn-warning'
      },
      {
        text: 'New Receiver <i class="fas fa-plus"></i>',
        // text: 'New Campaign',
        action: function ( e, dt, node, config ) {
          $('#newDonationReceiverModal').modal('show');
        },
        className: 'btn btn-secondary'
      }
    ],
    order: [[0, 'desc']]
  });

  // Thêm custom filter cho ID
  $.fn.dataTable.ext.search.push(
    function(settings, data, dataIndex) {
      const searchInput = document.querySelector('#campaignId');
      const searchStr = searchInput.value;

      const searchId = parseInt(searchStr, 10);
      const rowId = parseInt(data[0], 10); // Giả sử ID nằm ở cột 0

      if (isNaN(searchId) || isNaN(rowId)) return true;

      return searchId === rowId;
    }
  );

  // Gọi lại filter mỗi khi người dùng thay đổi ID
  document.querySelector('#campaignId').addEventListener('input', function () {
    table.draw();
  });
});