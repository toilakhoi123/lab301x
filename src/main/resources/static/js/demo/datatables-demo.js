$(document).ready(function() {
  var table = $('#dataTable').DataTable();

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