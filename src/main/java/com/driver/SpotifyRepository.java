package com.driver;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public List<User> getUsers() {
        return users;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User();
        user.setName(name);
        user.setMobile(mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist();
        artist.setName(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Album album = new Album();
        album.setTitle(title);
        album.setArtistName(artistName);
        albums.add(album);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        List<Album> albums = getAlbums();
        Optional<Album> album = albums.stream().filter(album1 -> album1.getTitle().equals(albumName)).findAny();
        if(album.isEmpty()){
            throw new Exception("Album does not exist");
        }
        Song song = new Song();
        song.setTitle(title);
        song.setAlbumName(albumName);
        songs.add(song);
        albumSongMap.put(album.get(),songs);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist = createPlayList(title);
        List<Song> songs = getSongs();
        List<Song> playListSongs = songs.stream().filter(song -> song.getLength() == length).collect(Collectors.toList());
        playlist.setSongs(playListSongs);
        User user = getUserByMobile(mobile);
        if(user==null){
            throw new Exception("User does not exist");
        }
        playlist.setCreator(user);
        playlists.add(playlist);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist = createPlayList(title);
        List<Song> songs = getSongs();
        List<Song> playListSongs = songs.stream().filter(song -> songTitles.contains(song.getTitle()))
                .collect(Collectors.toList());
        playlist.setSongs(playListSongs);
        User user = getUserByMobile(mobile);
        if(user==null){
            throw new Exception("User does not exist");
        }
        playlist.setCreator(user);
        playlists.add(playlist);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        List<Playlist> playlists = getPlaylists();
        Playlist playlist = playlists.stream().filter(playlist1 -> playlist1.getTitle().equals(playlistTitle)).findAny()
                .orElseThrow(()-> new Exception("Playlist does not exist"));

        if (!playlist.getCreator().getMobile().equals(mobile)) {
            throw new Exception("User does not exist");
        }

        return playlist;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        List<Song> songs = getSongs();
        Song song = songs.stream().filter(song1 -> song1.getTitle().equals(songTitle)).findAny().orElseThrow(()->{
            return new Exception("Song does not exist");
        });

        List<String> likedUsers = song.getLikedUsers();
        if (!likedUsers.contains(mobile)) {
            song.getLikedUsers().add(mobile);
            song.setLikes(song.getLikes() + 1);
            List<Album> albums = getAlbums();
            Album album = albums.stream().filter(album1 -> album1.getTitle().equals(song.getAlbumName()))
                    .findAny().orElse(null);
            if (album != null) {
                Artist artist = getArtists().stream().filter(artist1 -> artist1.getName().equals(album.getArtistName()))
                        .findAny().orElse(null);
                if (!artist.getLikedUsers().contains(mobile)) {
                    artist.setLikes(artist.getLikes() + 1);
                    artist.getLikedUsers().add(mobile);
                }
            }
        }

        return song;
    }

    public String mostPopularArtist() {
        List<Artist> artists = getArtists();
        artists.sort(Comparator.comparing(Artist::getLikes).reversed());
        return artists.get(0).getName();
    }

    public String mostPopularSong() {
        List<Song> songs = getSongs();
        songs.sort(Comparator.comparing(Song::getLikes).reversed());
        return songs.get(0).getTitle();
    }
    private Playlist createPlayList(String title){
        Playlist playlist = new Playlist();
        playlist.setTitle(title);
        return playlist;
    }
    private User getUserByMobile(String mobile){
        List<User> users = getUsers();
        Optional<User> user = users.stream().filter(user1 -> user1.getMobile().equals(mobile)).findAny();
        return user.orElse(null);
    }
}
