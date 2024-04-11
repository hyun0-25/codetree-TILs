import java.awt.geom.Area;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 * 시작: 22:53
 * 종료:
 * 
 * 문제해석
 * 0~5사이 숫자 적힌 nXn격자
 * 0:빈공간
 * 1:사무실
 * 2:에어컨 좌
 * 3:에어컨 상
 * 4:에어컨 우 
 * 5:에어컨 하
 * 
 * 에어컨 사무실 시원하게 하는 과정
 * 1. 45도 기준으로 퍼짐, 벽이 있으면 전파x
 * 	45도,정면,45도
 * 	새로운 시원함의 생성==모든 에어컨으로부터 나오는 시원함의 합
 * 		에어컨이 놓여있는 자리에도 시원함 생성 가능
 * 2. 시원한 공기 섞기
 * 	서로 인접한 칸들에 대해 시원함이 높->낮으로 전파(시원함의 차이/4:내림)
 * 	모든 칸이 동시에 발생, 벽이 있는 칸끼리는 X
 * 3. 외벽에 있는 칸에 대해서만 시원함 -1 감소
 * 	바깥쪽과 맞닿아있는 칸들, 단 이미 시원함이 0인 칸은 감소X
 * 
 * 모든 사무실에서의 시원함이 K이상일때까지 반복
 * 
 * 
 * 입력
 * 첫째줄: n격자크기,m벽개수,k원하는사무실 시원함정도
 * n개줄: 격자판 정보
 * m개줄: 벽정보 x,y,s (1이면 x,y바로위 벽, 2이면 x,y왼쪽벽)
 * 
 * 출력
 * 모든 사무실에서의 시원함이 K이상인 최초의 시간
 * 
 * 문제해결프로세스
 * 1. cool()
 * 
 * 2. spread()
 * 	사이에 벽있는지
 * 	상->하  x,y=1
 * 	하->상 dx,dy=1
 * 	좌->우 x,y=2
 * 	우->좌 dx,dy=2
 * 3. edge()
 * 	외벽 > 0 이면 -1
 * 4. good()
 * 	모든 사무실이 k이상이면 true
 * 		아니면 false
 * 
 * 시간복잡도
 * 
 */
public class Main {
	static int n,m,k;
	static boolean wall[][][];
	static int map[][], morecool[][];
	static List<Cooler> list = new ArrayList<>();
	static class Cooler {
		int x,y,d;

		public Cooler(int x, int y, int d) {
			super();
			this.x = x;
			this.y = y;
			this.d = d;
		}
	}
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		n = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken());
		k = Integer.parseInt(st.nextToken());
		morecool = new int[n][n];
		map = new int[n][n];
		wall = new boolean[n][n][4];
		for (int i = 0; i < n; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < n; j++) {
				int dir = Integer.parseInt(st.nextToken());
				map[i][j] = dir;
				if(dir==2) { //좌
					list.add(new Cooler(i,j,6));
				}
				else if(dir==3) { //상
					list.add(new Cooler(i,j,0));
				}
				else if(dir==4) { //우
					list.add(new Cooler(i,j,2));
				}
				else if(dir==5) { //하
					list.add(new Cooler(i,j,4));
				}
			}
		}
		for (int i = 0; i < m; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken())-1;
			int y = Integer.parseInt(st.nextToken())-1;
			int d = Integer.parseInt(st.nextToken());
			if(d==0) {
				wall[x][y][0]=true; //상
				wall[x-1][y][2]=true; //하
				
			}
			else if(d==1) {
				wall[x][y][3]=true;
				wall[x][y-1][1]=true;
			}
		}
		int time = 0;
		while(true) {
			cool();
			spread();
			edge();
			time++;
			if(good()) break;
		}
		System.out.println(time);
	}
	 /* 문제해결프로세스
	 * 1. cool()
	 * 
	 * 2. spread()
	 * 	사이에 벽있는지
	 * 	상->하  x,y=1
	 * 	하->상 dx,dy=1
	 * 	좌->우 x,y=2
	 * 	우->좌 dx,dy=2
	 * 3. edge()
	 * 	외벽 > 0 이면 -1
	 * 4. good()
	 * 	모든 사무실이 k이상이면 true
	 * 		아니면 false
	 */
	public static boolean good() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if(map[i][j]==1 && morecool[i][j]<k) {
					return false;
				}
			}
		}
		return true;
	}
	public static void edge() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if(i==0 || j==0 || i==n-1 || j==n-1) {
					if(morecool[i][j]>0) morecool[i][j]--;
				}
			}
		}

	}
	public static void spread() {
		int copycool[][] = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < 8; k+=2) {
					int nx = i+dx[k];
					int ny = j+dy[k];
					if(!rangecheck(nx,ny) || wall[i][j][k/2]) continue;
					
					int a = morecool[i][j];
					int b = morecool[nx][ny];
					int val = (b-a)/4;
					copycool[i][j]+=val;
					
				}
			}
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				morecool[i][j]+=copycool[i][j];
			}
		}
	}
	static class Pair {
		int x,y;

		public Pair(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
	}
	static int dx[] = {-1,-1,0,1,1,1,0,-1};
	static int dy[] = {0,1,1,1,0,-1,-1,-1};
	public static void cool() {
		for (int i = 0; i < list.size(); i++) {
			Cooler c = list.get(i);
			int x = c.x;
			int y = c.y;
			int d = c.d;
			int d1 = c.d-1;
			if(d1<0) d1+=8;
			int d2 = c.d+1;
			if(d2>=8) d2-=8;
			boolean visited[][] = new boolean[n][n];
			x += dx[d];
			y += dy[d];
			Queue<Pair> q = new ArrayDeque<>(); 
			q.offer(new Pair(x, y));
			visited[x][y] = true;
			int depth = 5;
			while(!q.isEmpty()) {
				int size = q.size();
				for (int j = 0; j < size; j++) {
					Pair p = q.poll();
					int xx = p.x;
					int yy = p.y;
					morecool[xx][yy] += depth;
					//3방 숫자넣기
						
					int nx = xx+dx[d];
					int ny = yy+dy[d];
					
					if(rangecheck(nx,ny) && !visited[nx][ny] && !wall[xx][yy][d/2]) {
						q.add(new Pair(nx,ny));
						visited[nx][ny]=true;
					}
					
					int nx1 = xx+dx[d1];
					int ny1 = yy+dy[d1];
					int nx2 = xx+dx[d2];
					int ny2 = yy+dy[d2];
					int dd = d/2+2;
					if(dd>=4) dd-=4;
					int dd1 = 0;
					int dd2 = 0;
					if(d==0 || d==4) {
						dd1 = dd+1;
						if(dd1>=4) dd1-=4;
						dd2 = dd-1;
						if(dd2<0) dd2+=4;
						
					}
					else {
						dd1 = dd-1;
						if(dd1<0) dd1+=4;
						dd2 = dd+1;
						if(dd2>=4) dd2-=4;
					}
					if(rangecheck(nx1,ny1) && !visited[nx1][ny1] && !wall[xx][yy][dd1] && !wall[nx1][ny1][dd]) {
						q.add(new Pair(nx1,ny1));
						visited[nx1][ny1]=true;

					}
					
					if(rangecheck(nx2,ny2) && !visited[nx2][ny2] && !wall[xx][yy][dd2] && !wall[nx2][ny2][dd]) {
						q.add(new Pair(nx2,ny2));
						visited[nx2][ny2]=true;

					}
				}
				depth--;
				if(depth==0) break;
			}
		}
	}
	public static boolean rangecheck(int rx, int ry) {
		return rx>=0 && rx<n && ry>=0 && ry<n;
	}

}