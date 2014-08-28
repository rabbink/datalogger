package nl.rabbink.datalogger.rest;

import com.google.gson.Gson;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import nl.rabbink.datalogger.dao.DAO;
import nl.rabbink.datalogger.dao.impl.ReadingDAO;
import nl.rabbink.datalogger.model.Reading;
import nl.rabbink.datalogger.model.Result;

@Path("readings")
public class ReadingsResource {

    @Context
    private ServletContext context;

    @GET
    @Path("/graph")
    @Produces(MediaType.APPLICATION_JSON)
    public String getGraphReadings(@QueryParam("start") Long start, @QueryParam("end") Long end) {
        DAO readingDao = ReadingDAO.getInstance();

        List<Reading> readings;
        if (start != null && end != null) {
            readings = readingDao.list(start, end);
        } else {
            readings = readingDao.list();
        }

        List<List> data = new ArrayList<>();
        for (Reading reading : readings) {
            List<Number> values = new ArrayList<>();
            values.add(reading.getTimestamp().getTime());
            values.add(reading.getValue());
            data.add(values);
        }

        return new Gson().toJson(data);
    }

    @GET
    @Path("/table")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTableReadings(@DefaultValue("1") @QueryParam("sEcho") int sEcho,
            @DefaultValue("0") @QueryParam("iDisplayStart") int offset,
            @DefaultValue("10") @QueryParam("iDisplayLength") int limit,
            @DefaultValue("1") @QueryParam("iColumns") int columns,
            @DefaultValue("0") @QueryParam("iSortCol_0") int sortColumn,
            @DefaultValue("desc") @QueryParam("sSortDir_0") String sortDirection,
            @DefaultValue("1") @QueryParam("iSortingCols") int columnsToSort) {

        TableColumn orderBy = TableColumn.getByDatatablesColumnId(sortColumn);
        SortOrder sortOrder = SortOrder.findByName(sortDirection);

        ReadingDAO readingDao = ReadingDAO.getInstance();

        Result<Reading> result = readingDao.list(limit, offset, orderBy, sortOrder);

        // TODO fix formatting according to set locale
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        NumberFormat nf = new DecimalFormat("#0.0");

        List<List> data = new ArrayList<>();
        for (Reading reading : result.getValues()) {
            List<String> values = new ArrayList<>();

            String formattedDate = sdf.format(reading.getTimestamp());
            values.add(formattedDate);

            values.add(nf.format(reading.getValue()));
            data.add(values);
        }

        TableData tableData = new TableData(sEcho, result.getCount(), result.getCount(), data);

        return new Gson().toJson(tableData);
    }

    private class TableData {

        private int sEcho;

        private int iTotalRecords;

        private int iTotalDisplayRecords;

        private List<List> aaData;

        public TableData(int sEcho, int iTotalRecords, int iTotalDisplayRecords, List<List> aaData) {
            this.sEcho = sEcho;
            this.iTotalRecords = iTotalRecords;
            this.iTotalDisplayRecords = iTotalDisplayRecords;
            this.aaData = aaData;
        }

        public int getsEcho() {
            return sEcho;
        }

        public int getiTotalRecords() {
            return iTotalRecords;
        }

        public int getiTotalDisplayRecords() {
            return iTotalDisplayRecords;
        }

        public List<List> getAaData() {
            return aaData;
        }
    }
}
