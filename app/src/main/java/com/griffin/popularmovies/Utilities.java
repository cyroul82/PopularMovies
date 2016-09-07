package com.griffin.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.griffin.popularmovies.Pojo.Cast;
import com.griffin.popularmovies.Pojo.Collection;
import com.griffin.popularmovies.Pojo.Credits;
import com.griffin.popularmovies.Pojo.Genre;
import com.griffin.popularmovies.Pojo.MovieDetail;
import com.griffin.popularmovies.Pojo.MovieImages;
import com.griffin.popularmovies.Pojo.Person;
import com.griffin.popularmovies.Pojo.ReviewPage;
import com.griffin.popularmovies.Pojo.Reviews;
import com.griffin.popularmovies.Pojo.Trailer;
import com.griffin.popularmovies.Pojo.TrailerDetail;
import com.griffin.popularmovies.Service.MovieService;
import com.griffin.popularmovies.adapter.CastingAdapter;
import com.griffin.popularmovies.data.MovieContract;
import com.griffin.popularmovies.detail_movie.DetailFavoriteFragment;
import com.griffin.popularmovies.detail_movie.DetailMovie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by griffin on 18/07/16.
 */
public class Utilities {

    public final static String LANGUAGE_SYSTEM = Locale.getDefault().getLanguage();
    private static final String LOG_TAG = Utilities.class.getSimpleName();
    private static final String ID_ITEM = "choice";

    public static DetailMovie getDetailMovieFromCursor(Cursor movieCursor) {
        DetailMovie detailMovie = new DetailMovie();

        detailMovie.getMovieDetail().setId(movieCursor.getInt(DetailFavoriteFragment.COL_FAVORITE_MOVIE_ID));
        detailMovie.getMovieDetail().setPosterPath(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_PICTURE));
        detailMovie.getMovieDetail().setReleaseDate(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DATE));
        detailMovie.getMovieDetail().setTitle(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_TITLE));
        detailMovie.getMovieDetail().setOriginalTitle(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_ORIGINAL_TITLE));
        detailMovie.getMovieDetail().setOverview(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_OVERVIEW));
        detailMovie.getMovieDetail().setVoteAverage(movieCursor.getDouble(DetailFavoriteFragment.COL_FAVORITE_MOVIE_RATING));

        detailMovie.getMovieDetail().setRuntime(movieCursor.getInt(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DETAIL_RUNTIME));

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();


        Type type = new TypeToken<List<Genre>>() {
        }.getType();
        String genreJSON = movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DETAIL_GENRE);
        List<Genre> genres = gson.fromJson(genreJSON, type);
        detailMovie.getMovieDetail().setGenres(genres);


        type = new TypeToken<List<TrailerDetail>>() {
        }.getType();
        String trailersJSON = movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DETAIL_VIDEOS);
        List<TrailerDetail> trailers = gson.fromJson(trailersJSON, type);
        detailMovie.setTrailerDetails(trailers);

        type = new TypeToken<List<Reviews>>() {
        }.getType();
        String reviewsJSON = movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DETAIL_REVIEWS);
        List<Reviews> reviews = gson.fromJson(reviewsJSON, type);
        detailMovie.setReviewsList(reviews);


        type = new TypeToken<List<Cast>>() {
        }.getType();
        String castingJSON = movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_DETAIL_CASTING);
        List<Cast> castList = gson.fromJson(castingJSON, type);
        detailMovie.getCredits().setCast(castList);

        detailMovie.getMovieDetail().setTagline(movieCursor.getString(DetailFavoriteFragment.COL_FAVORITE_MOVIE_TAGLINE));

        return detailMovie;
    }


    public static void addMovieToFavorite(DetailMovie detailMovie, Context context) {

        //long movieRowId;

        // First, check if the mMovie with this id already exists in the db
        Cursor movieCursor = context.getContentResolver().query(
                //The URI content://com.griffin.popularmovies :
                MovieContract.FavoriteEntry.CONTENT_URI,
                //The list of which columns to return, in this case only the _ID column
                new String[]{MovieContract.FavoriteEntry._ID},
                /* The filter returning only the row COLUMN_MOVIE_ID with the clause ? = movie_id(declared in the next parameter (selectionArgs)) */
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?",
                //only one clause movie_id
                new String[]{Long.toString(detailMovie.getMovieDetail().getId())},
                null);

        if (movieCursor != null) {
            if (movieCursor.moveToFirst()) {
                int movieIdIndex = movieCursor.getColumnIndex(MovieContract.FavoriteEntry._ID);
                //movieRowId = movieCursor.getLong(movieIdIndex);
            } else {

                ContentValues detail = new ContentValues();
                detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_TITLE, detailMovie.getMovieDetail().getTitle());

                Gson gson = new GsonBuilder().create();
                String casting = gson.toJson(detailMovie.getCredits().getCast());
                detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_CASTING, casting);

                detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_DATE, detailMovie.getMovieDetail().getReleaseDate());

                String genre = gson.toJson(detailMovie.getMovieDetail().getGenres());
                detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_GENRE, genre);

                detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_ORIGINAL_TITLE, detailMovie.getMovieDetail().getOriginalTitle());
                detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_OVERVIEW, detailMovie.getMovieDetail().getOverview());
                detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_RATING, Double.toString(detailMovie.getMovieDetail().getVoteAverage()));

                String reviews = gson.toJson(detailMovie.getReviewsList());
                detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_REVIEWS, reviews);
                detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_RUNTIME, detailMovie.getMovieDetail().getRuntime());

                String trailers = gson.toJson(detailMovie.getTrailerDetails());
                detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_TRAILER, trailers);
                detail.put(MovieContract.DetailEntry.COLUMN_MOVIE_TAGLINE, detailMovie.getMovieDetail().getTagline());

                // The resulting URI contains the ID for the row.  Extract the movieId from the Uri.
                Uri insertedDetailUri = context.getContentResolver().insert(MovieContract.DetailEntry.CONTENT_URI, detail);
                long insertedRowId = ContentUris.parseId(insertedDetailUri);

                // Now that the content provider is set up, inserting rows of data is pretty simple.
                // First create a ContentValues object to hold the data you want to insert.
                ContentValues values = new ContentValues();

                // Then add the data, along with the corresponding name of the data type,
                // so the content provider knows what kind of value is being inserted.
                values.put(MovieContract.FavoriteEntry.COLUMN_DETAIL_KEY, insertedRowId);
                values.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, detailMovie.getMovieDetail().getId());

                values.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_PICTURE, detailMovie.getMovieDetail().getPosterPath());

                // Finally, insert movie data into the database.
                //Uri insertedUri =
                context.getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, values);

            }

            movieCursor.close();
        }
    }


    public static void removeMovieFromFavorite(int idMovie, Context context) {
        context.getContentResolver().delete(MovieContract.FavoriteEntry.CONTENT_URI,
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
                new String[]{Integer.toString(idMovie)});
    }


    public static void setIdItem(Context context, int idItem) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ID_ITEM, idItem);
        editor.apply();
    }

    //Get back the String choice from the mSpinner
    public static int getIdItem(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(ID_ITEM, R.id.drawer_popular);
    }

    public static String getChoice(int idItem, Context context) {
        if (idItem == R.id.drawer_popular) {
            return context.getString(R.string.pref_movies_popular);
        }
        if (idItem == R.id.drawer_top_rated) {
            return context.getString(R.string.pref_movies_top_rated);
        }
        if (idItem == R.id.drawer_upcoming) {
            return context.getString(R.string.pref_movies_upcoming);
        }
        if (idItem == R.id.drawer_this_week) {
            return context.getString(R.string.pref_movies_now_playing);
        }
        if (idItem == R.id.drawer_favorite) {
            return context.getString(R.string.pref_movies_favorite);
        }
        if (idItem == R.string.search_title) {
            return context.getString(R.string.search_title);
        } else return null;
    }


    public static int isMovieFavorite(int idMovie, Context context) {
        int isFavorite = 0;
        Cursor cursor = context.getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI, null, MovieContract.FavoriteEntry
                .COLUMN_MOVIE_ID + " = ?", new String[]{Integer.toString(idMovie)}, null);
        if (cursor != null && cursor.moveToFirst()) {
            isFavorite = 1;
            cursor.close();
        }

        return isFavorite;
    }

    //Save picture into the mobile within the app folder (MODE PRIVATE)
    public static String savePoster(Bitmap bitmapImage, int idMovie, Context context) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        // path to /data/data/myapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File bitmap = new File(directory, Integer.toString(idMovie));

        FileOutputStream fos;
        try {
            //fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos = new FileOutputStream(bitmap);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        Log.i(LOG_TAG, directory.getAbsolutePath());
        return directory.getAbsolutePath();
    }

    //get the movie picture saved in the app folder
    public static Bitmap getPoster(String path, int idMovie) throws FileNotFoundException {
        Bitmap bitmap;
        File file = new File(path, Integer.toString(idMovie));
        bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

        return bitmap;

    }

    public static String getMonthAndYear(String date) throws ParseException {
        // Creates the format style to match the json format
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.getDefault());
        String year;
        // Creates the date with the format previously created
        Date d = format.parse(date);
        // Instancie le calendrier
        Calendar cal = Calendar.getInstance();
        String month_name = month_date.format(cal.getTime());
        // Sets up the calendar
        cal.setTime(d);
        // gets back the year out of the date and cast it into a string
        year = month_name + " " + Integer.toString(cal.get(Calendar.YEAR));

        return year;
    }

    public static String getYear(String date) throws ParseException {
        // Creates the format style to match the json format
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String year;

        // Creates the date with the format previously created
        Date d = format.parse(date);
        // Instancie le calendrier
        Calendar cal = Calendar.getInstance();

        //String month_name = month_date.format(cal.getTime());
        // Sets up the calendar
        cal.setTime(d);
        // gets back the year out of the date and cast it into a string
        year = Integer.toString(cal.get(Calendar.YEAR));

        return year;
    }

    public static void checkConnectionStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                //Connected with wifi
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                //Connected with mobile
            }
        } else {
            //Not connected
            Toast.makeText(context, R.string.connectivity, Toast.LENGTH_LONG).show();
        }
    }


    public static DetailMovie getMovieDetail(int movieId, DetailMovie detailMovie, Context context) throws IOException {

        String MOVIE_BASE_URL = context.getResources().getString(R.string.BASE_URL);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieService movieService = retrofit.create(MovieService.class);

        Call<Trailer> callTrailer = movieService.getTrailer(movieId, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
        Response responseCallTrailer = callTrailer.execute();
        Trailer trailer = (Trailer) responseCallTrailer.body();

        List<TrailerDetail> trailerDetails = null;
        if (trailer != null) {
            trailerDetails = trailer.getResults();
        }
        detailMovie.setTrailerDetails(trailerDetails);

        Call<MovieDetail> callMovieDetail = movieService.getMovieDetail(movieId, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
        Response responseCallMovieDetail = callMovieDetail.execute();
        MovieDetail movieDetail = (MovieDetail) responseCallMovieDetail.body();
        detailMovie.setMovieDetail(movieDetail);

        Call<Credits> callCredits = movieService.getCredits(movieId, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
        Response responseCallCredits = callCredits.execute();
        Credits credits = (Credits) responseCallCredits.body();

        if (credits != null) {
            int castingMax;

            if (credits.getCast().size() < CastingAdapter.MAX_CASTING_TO_DISPLAY) {
                castingMax = credits.getCast().size() - 1;
            } else {
                castingMax = CastingAdapter.MAX_CASTING_TO_DISPLAY;
            }

            for (int i = 0; i < castingMax; i++) {
                Cast cast = credits.getCast().get(i);
                Call<Person> callPerson = movieService.getPerson(cast.getId(), BuildConfig.MOVIE_DB_API_KEY);
                Response responseCallPerson = callPerson.execute();
                Person person = (Person) responseCallPerson.body();
                cast.setPerson(person);
            }
        }

        detailMovie.setCredits(credits);

        Call<ReviewPage> callReviewPage = movieService.getReviews(movieId, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
        Response responseCallReviewPage = callReviewPage.execute();
        ReviewPage reviewPage = (ReviewPage) responseCallReviewPage.body();
        List<Reviews> reviewsList = null;
        if (reviewPage != null) {
            reviewsList = reviewPage.getResults();
        }

        detailMovie.setReviewsList(reviewsList);


        Object collectionObject = detailMovie.getMovieDetail().getBelongsToCollection();
        if (collectionObject != null) {
            Map<String, ?> map = (Map<String, ?>) collectionObject;
            Object object = map.get("id");
            double idCollection = Double.parseDouble(object.toString());
            Call<Collection> callCollection = movieService.getCollection(idCollection, BuildConfig.MOVIE_DB_API_KEY, LANGUAGE_SYSTEM);
            Response responseCallCollection = callCollection.execute();
            Collection collection = (Collection) responseCallCollection.body();

            detailMovie.setCollection(collection);

        }

        Call<MovieImages> callMovieImages = movieService.getMovieImages(movieId, BuildConfig.MOVIE_DB_API_KEY);
        Response responseCallMovieImages = callMovieImages.execute();
        MovieImages movieImages = (MovieImages) responseCallMovieImages.body();

        detailMovie.setMovieImages(movieImages);


        return detailMovie;
    }

    //used for the recyclerView filmography autofit column
    //http://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns  RITEN
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }


}
