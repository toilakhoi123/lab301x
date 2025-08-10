$(document).ready(function() {
  var table = $('#dataTable').DataTable({
    dom:  "<'row mb-2'<'col-sm-6 text-left'f><'col-sm-6 text-right'B>>" +
          "<'row'<'col-sm-12'tr>>" +
          "<'row'<'col-sm-5'i><'col-sm-7'p>>",
    buttons: [
      {
        filename: 'User Data',
        sheetName: 'User Data',
        extend: 'excelHtml5',
        text: '<i class="fas fa-file-excel"></i> Export to Excel',
        className: 'btn btn-success',
        exportOptions: {
          columns: [0, 1, 2, 3, 4, 5, 6, 7],
          format: {
            body: function (data, _, column, _) {
              // Logic for the 'role' column (column 6)
              if (column === 6) {
                // Create a temporary DOM element to parse the HTML string.
                const parser = new DOMParser();
                const doc = parser.parseFromString(data, 'text/html');

                // Find the option element with the 'selected' attribute and return its value.
                const selectedOption = doc.querySelector('select.role-dropdown option[selected]');
                return selectedOption ? selectedOption.value : '';
              }

              // Logic for the 'disabled' column (column 7)
              if (column === 7) {
                // Create a temporary DOM element to parse the HTML string.
                const parser = new DOMParser();
                const doc = parser.parseFromString(data, 'text/html');

                // Find the option element with the 'selected' attribute and return its value ('true' or 'false').
                const selectedOption = doc.querySelector('select.account-disabled-dropdown option[selected]');
                return selectedOption ? selectedOption.value : '';
              }

              // Default logic for other columns: remove <span> tags
              return data.replaceAll("<span>", "").replaceAll("</span>", "");
            }
          }
        }
      }
    ],
    order: [[0, 'desc']]
  });

  // Custom filter function
  $.fn.dataTable.ext.search.push(
    function(_, data, _) {
      var filterValue = $('#loginFilter').val();
      if (!filterValue) {
        return true;
      }

      // get 6th column
      var lastLogin = data[5];

      // never logged in -> always show
      if (lastLogin === "Never") {
        return true;
      }

      // invalid date
      var parts = lastLogin.split("/");
      if (parts.length !== 3) {
        return false;
      }

      // parse date
      var day = parseInt(parts[0], 10);
      var month = parseInt(parts[1], 10) - 1; // js date: 0-index system
      var year = parseInt(parts[2], 10);

      // calculate diff
      var loginDate = new Date(year, month, day);
      var now = new Date();
      var diffTime = Math.abs(now - loginDate);
      var diffDays = diffTime / (1000 * 60 * 60 * 24);

      return diffDays >= parseInt(filterValue);
    }
  );

  // Event listener to trigger filtering
  $('#loginFilter').on('change', function() {
    table.draw();
  });
});